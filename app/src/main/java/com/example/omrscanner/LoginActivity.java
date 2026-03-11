package com.example.omrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class LoginActivity extends AppCompatActivity {

    // ── Hardcoded dummy credentials ──────────────────────────────────────────
    private static final String DUMMY_USERNAME = "admin";
    private static final String DUMMY_PASSWORD = "admin123";
    // ─────────────────────────────────────────────────────────────────────────

    private EditText etUsername;
    private EditText etPassword;
    private ImageView ivPasswordToggle;
    private Button btnSignIn;
    private TextView tvLoginError;

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Full-screen (immersive)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        controller.hide(WindowInsetsCompat.Type.systemBars());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Bind views
        etUsername       = findViewById(R.id.etUsername);
        etPassword       = findViewById(R.id.etPassword);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
        btnSignIn        = findViewById(R.id.btnSignIn);
        tvLoginError     = findViewById(R.id.tvLoginError);

        // Password visibility toggle
        ivPasswordToggle.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPasswordToggle.setAlpha(0.85f);
            } else {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPasswordToggle.setAlpha(0.45f);
            }
            // Keep cursor at end
            etPassword.setSelection(etPassword.getText().length());
        });

        // Sign In button
        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Basic validation
        if (username.isEmpty()) {
            showError("Please enter your username.");
            shakeView(etUsername);
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            shakeView(etPassword);
            return;
        }

        // Check against dummy credentials
        if (username.equals(DUMMY_USERNAME) && password.equals(DUMMY_PASSWORD)) {
            clearError();
            // Navigate to Dashboard
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            showError("Incorrect username or password.");
            shakeView(findViewById(R.id.loginCard));
        }
    }

    private void showError(String message) {
        tvLoginError.setText(message);
        tvLoginError.setVisibility(View.VISIBLE);
    }

    private void clearError() {
        tvLoginError.setText("");
        tvLoginError.setVisibility(View.GONE);
    }

    /** Horizontal shake animation to indicate an error on a view. */
    private void shakeView(View view) {
        if (view == null) return;
        android.view.animation.TranslateAnimation anim = new android.view.animation.TranslateAnimation(
                0, 10, 0, 0);
        anim.setDuration(80);
        anim.setRepeatCount(5);
        anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }
}
