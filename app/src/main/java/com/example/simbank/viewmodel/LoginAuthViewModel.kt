import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.simbank.viewmodel.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "LoginAuthViewModel"

    // Expose login state to the UI
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    fun loginUser(email: String, password: String) {
        Log.d(TAG, "loginUser called with email: $email")

        // Basic validation
        if (email.isBlank() || password.isBlank()) {
            Log.e(TAG, "Validation failed: Email and password are required.")
            authResultState.value = AuthResult.Error("Email and password are required.")
            return
        }

        // Show loading
        authResultState.value = AuthResult.Loading
        Log.d(TAG, "Validation passed, starting Firebase login.")

        // Firebase signInWithEmailAndPassword
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login success
                    Log.d(TAG, "Login successful for email: $email")
                    authResultState.value = AuthResult.Success
                } else {
                    // Login failed
                    val errorMsg = task.exception?.localizedMessage ?: "Login failed."
                    Log.e(TAG, "Login failed for email: $email, error: $errorMsg")
                    authResultState.value = AuthResult.Error(errorMsg)
                }
            }
    }
}