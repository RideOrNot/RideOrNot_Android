import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.hanium.rideornot.BuildConfig
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.databinding.FragmentLogin1Binding


class LoginFragment : Fragment() {
    // ...

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var binding: FragmentLogin1Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        // TODO: 키값을 BuildConfig에 정의하면 소스 디코드 시 문자열이 노출된다고 함.. 대안 찾아보기
        val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(googleWebClientId)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
        // ...

        return binding.root
    }
    // ...
}







