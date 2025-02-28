import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.simbank.viewmodel.AuthResult
import com.google.firebase.auth.FirebaseAuth

class ForgotPassAuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "ForgotPassAuthViewModel"

    // Expose reset password state to the UI
    val authResultState = mutableStateOf<AuthResult>(AuthResult.Idle)

    fun resetPassword(email: String) {
        Log.d(TAG, "resetPassword called with email: $email")

        // Basic validation
        if (email.isBlank()) {
            Log.e(TAG, "Validation failed: Email is required.")
            authResultState.value = AuthResult.Error("Email is required.")
            return
        }

        // Show loading
        authResultState.value = AuthResult.Loading
        Log.d(TAG, "Validation passed, starting Firebase password reset.")

        // Firebase sendPasswordResetEmail
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Reset link sent
                    Log.d(TAG, "Reset link sent to email: $email")
                    authResultState.value = AuthResult.Success
                } else {
                    // Reset failed
                    val errorMsg = task.exception?.localizedMessage ?: "Reset failed."
                    Log.e(TAG, "Reset failed for email: $email, error: $errorMsg")
                    authResultState.value = AuthResult.Error(errorMsg)
                }
            }
    }
}