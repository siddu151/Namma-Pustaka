package com.example.nammapustaka.repository

import androidx.lifecycle.LiveData
import com.example.nammapustaka.database.AppDatabase
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.database.entity.LeaderboardEntity
import com.example.nammapustaka.database.entity.ReservationEntity
import com.example.nammapustaka.database.entity.ReviewEntity
import com.example.nammapustaka.database.entity.TransactionEntity
import com.example.nammapustaka.database.entity.UserEntity
import com.example.nammapustaka.models.TxStatus
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.network.GeminiApi
import com.example.nammapustaka.network.GeminiContent
import com.example.nammapustaka.network.GeminiGenConfig
import com.example.nammapustaka.network.GeminiPart
import com.example.nammapustaka.network.GeminiRequest
import androidx.room.withTransaction
import com.example.nammapustaka.notifications.LibraryNotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

/**
 * Single access point for Room + Gemini (repository pattern).
 * Heavy operations run on [Dispatchers.IO].
 */
class LibraryRepository(
    private val db: AppDatabase,
    private val geminiApi: GeminiApi,
    private val geminiApiKey: String,
    private val notificationHelper: LibraryNotificationHelper
) {
    private val users = db.userDao()
    private val books = db.bookDao()
    private val txs = db.transactionDao()
    private val reviews = db.reviewDao()
    private val leaderboard = db.leaderboardDao()
    private val reservations = db.reservationDao()

    suspend fun listStudents(): List<UserEntity> = withContext(Dispatchers.IO) {
        users.listStudents()
    }

    // --- Auth ---
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        className: String?
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            if (users.getByEmail(email.trim()) != null) {
                return@withContext Result.failure(IllegalStateException("Email already registered"))
            }
            val id = users.insert(
                UserEntity(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    role = role.name,
                    className = className?.trim()?.takeIf { it.isNotBlank() }
                )
            )
            if (role == UserRole.STUDENT) {
                leaderboard.upsert(LeaderboardEntity(studentId = id, pagesRead = 0, booksCompleted = 0))
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val u = users.getByEmail(email.trim().lowercase())
                ?: return@withContext Result.failure(IllegalArgumentException("User not found"))
            if (u.password != password) {
                return@withContext Result.failure(IllegalArgumentException("Wrong password"))
            }
            Result.success(u)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(id: Long): UserEntity? = withContext(Dispatchers.IO) {
        users.getById(id)
    }

    fun observeStudents() = users.observeStudents()

    suspend fun getActiveTransaction(bookId: Long): TransactionEntity? = withContext(Dispatchers.IO) {
        txs.getActiveForBook(bookId)
    }

    // --- Catalog ---
    fun observeBooks(query: String?, category: String?): LiveData<List<BookEntity>> {
        val q = query?.trim().orEmpty().ifBlank { "%" }
        val cat = if (category.isNullOrBlank()) "__ALL__" else category
        return books.observeSearch(q, cat)
    }

    suspend fun getBook(id: Long): BookEntity? = withContext(Dispatchers.IO) { books.getById(id) }

    suspend fun getBookByQr(qr: String): BookEntity? = withContext(Dispatchers.IO) {
        books.getByQr(qr.trim())
    }

    suspend fun addBook(
        title: String,
        author: String,
        category: String,
        description: String,
        imageUri: String?,
        totalPages: Int,
        qrCode: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val dup = books.findDuplicate(title, author)
            if (dup != null) {
                return@withContext Result.failure(IllegalStateException("Duplicate book (same title & author)"))
            }
            val id = books.insert(
                BookEntity(
                    title = title.trim(),
                    author = author.trim(),
                    category = category.trim(),
                    description = description.trim(),
                    imageUri = imageUri,
                    qrCode = qrCode.trim(),
                    available = true,
                    totalPages = totalPages.coerceAtLeast(0)
                )
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBook(book: BookEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            books.update(book)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBook(bookId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val active = txs.getActiveForBook(bookId)
            if (active != null) {
                return@withContext Result.failure(IllegalStateException("Book is currently issued"))
            }
            books.deleteById(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Issue / Return ---
    suspend fun issueBook(bookId: Long, studentUserId: Long, dueDate: Date): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                db.withTransaction {
                    val book = books.getById(bookId)
                        ?: throw IllegalArgumentException("Book missing")
                    if (!book.available) throw IllegalStateException("Book unavailable")
                    if (txs.getActiveForBook(bookId) != null) throw IllegalStateException("Already issued")
                    val now = Date()
                    txs.insert(
                        TransactionEntity(
                            bookId = bookId,
                            studentId = studentUserId,
                            issueDate = now,
                            dueDate = dueDate,
                            returnDate = null,
                            status = TxStatus.ISSUED
                        )
                    )
                    books.update(book.copy(available = false))
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun returnBook(bookId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var titleForNotif: String? = null
            var pendingStudent: Long? = null
            db.withTransaction {
                val tx = txs.getActiveForBook(bookId)
                    ?: throw IllegalStateException("No active issue for this book")
                val book = books.getById(bookId) ?: throw IllegalArgumentException("Book missing")
                val now = Date()
                txs.markReturned(tx.transactionId, now)
                books.update(book.copy(available = true))

                val lb = leaderboard.getForStudent(tx.studentId) ?: LeaderboardEntity(studentId = tx.studentId)
                val pages = book.totalPages.coerceAtLeast(0)
                leaderboard.upsert(
                    lb.copy(
                        pagesRead = lb.pagesRead + pages,
                        booksCompleted = lb.booksCompleted + 1
                    )
                )

                val pending = reservations.pendingForBook(bookId).firstOrNull()
                if (pending != null) {
                    titleForNotif = book.title
                    pendingStudent = pending.studentId
                }
            }
            if (titleForNotif != null && pendingStudent != null) {
                notificationHelper.showReservedBookAvailable(titleForNotif!!, pendingStudent!!)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTransactions() = txs.observeRecentRows()
    fun observeActiveBorrows() = txs.observeActiveBorrowRows()
    fun observeOverdue() = txs.observeOverdueRows()
    fun observeMyBorrows(studentId: Long) = txs.observeForStudent(studentId)

    // --- Reviews ---
    fun observeReviews(bookId: Long) = reviews.observeForBook(bookId)
    fun observeAverageRating(bookId: Long) = reviews.observeAverageRating(bookId)

    suspend fun addReview(bookId: Long, studentId: Long, rating: Float, text: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                reviews.insert(
                    ReviewEntity(
                        bookId = bookId,
                        studentId = studentId,
                        rating = rating.coerceIn(1f, 5f),
                        reviewText = text.trim()
                    )
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // --- Reservations ---
    fun observeMyReservations(studentId: Long) = reservations.observeForStudent(studentId)

    suspend fun reserveBook(bookId: Long, studentId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val book = books.getById(bookId) ?: return@withContext Result.failure(IllegalArgumentException("Book missing"))
            if (book.available) {
                return@withContext Result.failure(IllegalStateException("Book is already available — borrow instead"))
            }
            if (reservations.countActive(studentId, bookId) > 0) {
                return@withContext Result.failure(IllegalStateException("Already reserved"))
            }
            reservations.insert(ReservationEntity(bookId = bookId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Leaderboard ---
    fun observeLeaderboard(limit: Int = 50) = leaderboard.observeLeaderboard(limit)

    // --- Admin stats ---
    fun observeBookCount() = books.observeBookCount()
    fun observeStudentCount() = users.observeStudentCount()
    fun observeTransactionCount() = txs.observeTransactionCount()

    suspend fun adminCounts(): Triple<Int, Int, Int> = withContext(Dispatchers.IO) {
        Triple(books.countBooks(), users.countStudents(), txs.countTransactions())
    }

    // --- AI summary (Gemini) ---
    suspend fun generateKannadaSummary(bookId: Long): Result<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            if (geminiApiKey.isBlank()) {
                return@withContext Result.failure(IllegalStateException("Add GEMINI_API_KEY to local.properties"))
            }
            try {
                val book = books.getById(bookId)
                    ?: return@withContext Result.failure(IllegalArgumentException("Book missing"))
                val prompt = buildString {
                    append("You are Namma-Pustaka, a rural Karnataka school librarian AI.\n")
                    append("Book: ${book.title} by ${book.author} (${book.category}).\n")
                    append("Description: ${book.description}\n\n")
                    append("Write a short Kannada summary for middle-school readers.\n")
                    append("Also output ONE line starting exactly with 'DIFFICULTY:' in Kannada ")
                    append("describing reading difficulty (e.g. ಸುಲಭ / ಮಧ್ಯಮ / ಕಠಿಣ).\n")
                    append("Keep the Kannada simple and encouraging.")
                }
                val body = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    generationConfig = GeminiGenConfig(temperature = 0.35)
                )
                val resp = geminiApi.generateContent(geminiApiKey, body)
                val text = resp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext Result.failure(IllegalStateException("Empty AI response"))
                val difficulty = text.lineSequence()
                    .firstOrNull { it.trim().startsWith("DIFFICULTY:", ignoreCase = true) }
                    ?.substringAfter(":")
                    ?.trim()
                    ?: "ಮಧ್ಯಮ"
                val summaryBody = text.lines().filterNot {
                    it.trim().startsWith("DIFFICULTY:", ignoreCase = true)
                }.joinToString("\n").trim()

                val updated = book.copy(
                    kannadaSummary = summaryBody,
                    readingDifficulty = difficulty
                )
                books.update(updated)
                Result.success(summaryBody to difficulty)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Inserts demo users, books, and sample stats when the database is empty.
     */
    suspend fun ensureSeedData(): Unit = withContext(Dispatchers.IO) {
        if (users.countAll() > 0) return@withContext

        users.insert(
            UserEntity(
                name = "ಶ್ರೀಮತಿ ಲಕ್ಷ್ಮಿ (ಗ್ರಂಥಪಾಲಕರು)",
                email = "admin@nammapustaka.edu",
                password = "admin123",
                role = UserRole.ADMIN.name,
                className = null,
                imageUri = null
            )
        )
        val s1 = users.insert(
            UserEntity(
                name = "ಅನಿಲ್ ಕುಮಾರ್",
                email = "anil@nammapustaka.edu",
                password = "student123",
                role = UserRole.STUDENT.name,
                className = "9 A",
                imageUri = null
            )
        )
        val s2 = users.insert(
            UserEntity(
                name = "ಸೌಮ್ಯಾ",
                email = "soumya@nammapustaka.edu",
                password = "student123",
                role = UserRole.STUDENT.name,
                className = "8 B",
                imageUri = null
            )
        )
        leaderboard.upsert(LeaderboardEntity(studentId = s1, pagesRead = 120, booksCompleted = 2))
        leaderboard.upsert(LeaderboardEntity(studentId = s2, pagesRead = 80, booksCompleted = 1))

        fun book(title: String, author: String, cat: String, pages: Int, desc: String, qr: String) =
            BookEntity(
                title = title,
                author = author,
                category = cat,
                description = desc,
                imageUri = null,
                qrCode = qr,
                available = true,
                totalPages = pages
            )

        val b1 = books.insert(
            book(
                "Elements of Science",
                "R. Sharma",
                "Science",
                210,
                "Introductory science concepts with simple experiments.",
                "NP-${UUID.randomUUID()}"
            )
        )
        val b2 = books.insert(
            book(
                "Freedom Chronicles",
                "K. Patil",
                "History",
                300,
                "Stories from India's independence movement adapted for schools.",
                "NP-${UUID.randomUUID()}"
            )
        )
        val b3 = books.insert(
            book(
                "Moral Tales",
                "S. Rao",
                "Story",
                95,
                "Short moral stories for daily reading.",
                "NP-${UUID.randomUUID()}"
            )
        )

        reviews.insert(
            ReviewEntity(
                bookId = b1,
                studentId = s1,
                rating = 4.5f,
                reviewText = "Very helpful diagrams!"
            )
        )
    }
}
