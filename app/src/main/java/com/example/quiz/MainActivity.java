package com.example.quiz;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int QUIZ_SIZE = 5;   // пункт 8: обмежена кількість

    private TextView questionText, scoreText;
    private Button trueButton, falseButton;

    private String[] questions, answers;
    private final List<Integer> quiz = new ArrayList<>(); // індекси обраних питань
    private boolean[] answered;
    private int pos = 0, score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getStringArray(R.array.answers);

        // пункт 8: випадкова вибірка та обмеження кількості
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) all.add(i);
        Collections.shuffle(all);
        quiz.addAll(all.subList(0, Math.min(QUIZ_SIZE, all.size())));
        answered = new boolean[quiz.size()];

        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        ImageButton prev = findViewById(R.id.prev_button);
        ImageButton next = findViewById(R.id.next_button);

        trueButton.setOnClickListener(v -> checkAnswer(true));
        falseButton.setOnClickListener(v -> checkAnswer(false));
        next.setOnClickListener(v -> move(1));
        prev.setOnClickListener(v -> move(-1));

        showQuestion();
    }

    private void showQuestion() {
        int currentQuizIndex = quiz.get(pos);
        boolean isAnswered = answered[pos];

        if (isAnswered) {
            // Якщо на питання вже відповідали раніше, показуємо збережений стан (відповідь та колір)
            boolean correctValue = Boolean.parseBoolean(answers[currentQuizIndex]);
            // Визначаємо, чи була відповідь правильною для фарбування тексту
            questionText.setText(correctValue ? R.string.answer_yes : R.string.answer_no);
            // Блокуємо кнопки відповіді для вже пройденого питання
            setButtonsEnabled(false);
        } else {
            // Якщо питання нове, показуємо звичайний текст запитання
            questionText.setTextColor(Color.BLACK);
            questionText.setText(questions[currentQuizIndex]);
            setButtonsEnabled(true);
        }
        updateScore();
    }

    private void checkAnswer(boolean userSaysTrue) {
        if (answered[pos]) return; // Запобігаємо повторній відповіді

        int currentQuizIndex = quiz.get(pos);
        boolean correctValue = Boolean.parseBoolean(answers[currentQuizIndex]);
        boolean isCorrect = (userSaysTrue == correctValue);

        if (isCorrect) score++;
        answered[pos] = true;
        setButtonsEnabled(false);

        // пункт 3: замість питання — відповідь, колір залежить від правильності
        questionText.setText(correctValue ? R.string.answer_yes : R.string.answer_no);
        questionText.setTextColor(isCorrect ? Color.rgb(0, 150, 0) : Color.RED);

        // пункт 2: Toast зверху
        Toast toast = Toast.makeText(this,
                isCorrect ? R.string.correct_toast : R.string.incorrect_toast,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();

        updateScore();

        // Автоматична перевірка: якщо на всі питання відповіли, показуємо результат
        if (allAnswered()) {
            showResult();
        }
    }

    // Навігація вперед та назад з урахуванням вибору
    private void move(int step) {
        if (allAnswered()) {
            showResult();
            return;
        }
        int n = quiz.size();
        int loops = 0;
        do {
            pos = (pos + step + n) % n;
            loops++;
            // Додаємо захист від нескінченного циклу, якщо всі питання пройдені
            if (loops > n) break;
        } while (false); // Можна залишити перемикання між усіма згенерованими питаннями

        showQuestion();
    }

    private boolean allAnswered() {
        for (boolean a : answered) {
            if (!a) return false;
        }
        return true;
    }

    private void showResult() {
        questionText.setTextColor(Color.BLUE);
        questionText.setText(getString(R.string.result_format, score, quiz.size()));
        setButtonsEnabled(false);
    }

    private void updateScore() {
        scoreText.setText(getString(R.string.score_format, score, quiz.size()));
    }

    private void setButtonsEnabled(boolean enabled) {
        trueButton.setEnabled(enabled);
        falseButton.setEnabled(enabled);
    }
}