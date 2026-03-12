package com.example.omrscanner;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class LoginActivity extends AppCompatActivity {

    private static final String DUMMY_USERNAME = "admin";
    private static final String DUMMY_PASSWORD = "admin123";

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

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        etUsername       = findViewById(R.id.etUsername);
        etPassword       = findViewById(R.id.etPassword);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
        btnSignIn        = findViewById(R.id.btnSignIn);
        tvLoginError     = findViewById(R.id.tvLoginError);

        // ── Kill XML backgroundTint so our drawable has full control ──────────
        // android:backgroundTint in XML sits on top of setBackground() — must null it.
        btnSignIn.setBackgroundTintList(null);
        setButtonGradient(false); // apply initial normal blue gradient
        // ─────────────────────────────────────────────────────────────────────

        // ── Button press color + scale feedback ──────────────────────────────
        btnSignIn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setButtonGradient(true);
                    animateButtonScale(btnSignIn, 0.96f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    setButtonGradient(false);
                    animateButtonScale(btnSignIn, 1.0f);
                    break;
            }
            return false; // let onClick still fire
        });

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
            etPassword.setSelection(etPassword.getText().length());
        });

        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    /**
     * Builds and applies a GradientDrawable directly as the button background.
     * Calls setBackgroundTintList(null) both before and after to ensure Android's
     * tint layer never overrides our custom drawable color.
     */
    private void setButtonGradient(boolean pressed) {
        btnSignIn.setBackgroundTintList(null);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(42f); // 14dp corner radius

        if (pressed) {
            bg.setColor(ContextCompat.getColor(this, R.color.gradient_pressed));
        } else {
            bg.setOrientation(GradientDrawable.Orientation.TL_BR);
            bg.setColors(new int[]{
                    ContextCompat.getColor(this, R.color.gradient_start),
                    ContextCompat.getColor(this, R.color.gradient_end)
            });
        }

        btnSignIn.setBackground(bg);
        btnSignIn.setBackgroundTintList(null); // clear again — setBackground() can re-trigger tinting
    }

    private void animateButtonScale(View view, float scale) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        scaleX.setDuration(80);
        scaleY.setDuration(80);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.start();
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

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

        if (username.equals(DUMMY_USERNAME) && password.equals(DUMMY_PASSWORD)) {
            clearError();
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