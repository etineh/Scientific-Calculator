package ng.com.topstar.topcalculator

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalStorageUtils {
//
//    private const val PREFS_NAME = "app_prefs"
//    private const val KEY_THEME_MODE = "theme_mode"
//
//    fun saveThemeMode(context: Context, isDarkMode: Boolean) {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        prefs.edit().putBoolean(KEY_THEME_MODE, isDarkMode).apply()
//    }
//
//    fun isDarkModeEnabled(context: Context): Boolean {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return prefs.getBoolean(KEY_THEME_MODE, false) // Default is Light Mode (false)
//    }

    fun saveLightMode(context: Context) {
        val sharedPref = context.getSharedPreferences(KVal.THEME_REF, Context.MODE_PRIVATE)

        with(sharedPref.edit()) { putString(KVal.NIGHT_MODE, KVal.LIGHT)
            apply()
        }
        // Apply the theme immediately
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun saveDarkMode(context: Context) {
        val sharedPref = context.getSharedPreferences(KVal.THEME_REF, Context.MODE_PRIVATE)

        with(sharedPref.edit()) { putString(KVal.NIGHT_MODE, KVal.DARK)
            apply()
        }
        // Apply the theme immediately
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    fun setToDefaultAppearance(context: Context) {
        val sharedPref = context.getSharedPreferences(KVal.THEME_REF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(KVal.NIGHT_MODE) // Remove the saved mode
            apply()
        }
        // Apply the system default night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun saveInput(context: Context, input: String) {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KVal.CAL_INPUT, input).apply()
    }
    fun getInput(context: Context): String {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        return sharedPref.getString(KVal.CAL_INPUT, "") ?: ""
    }

    fun clearInput(context: Context) {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        sharedPref.edit().remove(KVal.CAL_INPUT).apply()
    }

    fun saveCalculatorHistory(context: Context, calculatorHistoryM: CalculatorHistoryM) {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()

        // Retrieve existing history
        val historyJson = sharedPref.getString(KVal.CAL_HISTORY, "[]")
        val type = object : TypeToken<MutableList<CalculatorHistoryM>>() {}.type
        val historyList: MutableList<CalculatorHistoryM> = gson.fromJson(historyJson, type) ?: mutableListOf()

        // Add new history entry
        historyList.add(calculatorHistoryM)

        // Keep only the last 20 entries
        if (historyList.size > 20) {
            historyList.removeAt(0) // Remove the oldest entry
        }

        // Save updated history
        editor.putString(KVal.CAL_HISTORY, gson.toJson(historyList))
        editor.apply()
    }

    fun getCalculatorHistory(context: Context): MutableList<CalculatorHistoryM> {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve saved JSON string
        val historyJson = sharedPref.getString(KVal.CAL_HISTORY, "[]")

        // Convert JSON to MutableList<CalculatorHistoryM>
        val type = object : TypeToken<MutableList<CalculatorHistoryM>>() {}.type
        return gson.fromJson(historyJson, type) ?: mutableListOf()
    }

    fun clearCalculatorHistory(context: Context) {
        val sharedPref = context.getSharedPreferences(KVal.APP_PREFERENCE, Context.MODE_PRIVATE)
        sharedPref.edit().remove(KVal.CAL_HISTORY).apply()
    }

}