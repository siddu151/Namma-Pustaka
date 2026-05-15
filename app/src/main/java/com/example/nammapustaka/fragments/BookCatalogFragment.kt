package com.example.nammapustaka.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.adapters.BookGridAdapter
import com.example.nammapustaka.databinding.FragmentBookCatalogBinding
import com.example.nammapustaka.models.BookCategories
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.viewmodel.AppViewModelFactory

class BookCatalogFragment : Fragment() {

    private var _binding: FragmentBookCatalogBinding? = null
    private val binding get() = _binding!!

    private val vm: BookCatalogViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val text = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) binding.searchView.setQuery(text, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val session = (requireActivity().application as NammaPustakaApp).sessionManager

        val adapter = BookGridAdapter { book ->
            val bundle = Bundle().apply { putLong("bookId", book.bookId) }
            findNavController().navigate(R.id.bookDetailFragment, bundle)
        }
        binding.rvBooks.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvBooks.adapter = adapter

        val cats = mutableListOf("All")
        cats.addAll(BookCategories.ALL)
        binding.spCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cats)
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                vm.setCategory(if (pos == 0) null else cats[pos])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        vm.books.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                vm.setQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vm.setQuery(newText.orEmpty())
                return true
            }
        })

        binding.swipe.setOnRefreshListener {
            vm.setQuery(binding.searchView.query?.toString().orEmpty())
            binding.swipe.isRefreshing = false
        }

        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(R.menu.menu_catalog)
        binding.toolbar.menu.findItem(R.id.action_add_book).isVisible = session.role == UserRole.ADMIN
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_voice_search -> {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_books_hint))
                    }
                    try {
                        voiceLauncher.launch(intent)
                    } catch (_: Exception) {
                        Toast.makeText(requireContext(), "Voice search unavailable", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.action_add_book -> {
                    findNavController().navigate(R.id.addEditBookFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
