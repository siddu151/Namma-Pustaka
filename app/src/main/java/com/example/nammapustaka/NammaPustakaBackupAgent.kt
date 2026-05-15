package com.example.nammapustaka

import android.app.backup.BackupAgentHelper
import android.app.backup.SharedPreferencesBackupHelper

class NammaPustakaBackupAgent : BackupAgentHelper() {
    override fun onCreate() {
        // Optional: keep as placeholder for future app prefs.
        addHelper("prefs", SharedPreferencesBackupHelper(this, "nammapustaka_prefs"))
    }
}

