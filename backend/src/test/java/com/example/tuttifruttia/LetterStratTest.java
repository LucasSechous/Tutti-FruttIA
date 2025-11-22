package com.example.tuttifruttia;

import com.example.tuttifruttia.domain.ai.*;
import com.example.tuttifruttia.domain.core.*;
import com.example.tuttifruttia.domain.letter.Alphabet;
import com.example.tuttifruttia.domain.letter.LetterStrategy;
import com.example.tuttifruttia.domain.letter.UniformLetterStrategy;
import com.example.tuttifruttia.domain.persistence.PersistenceFactory;
import com.example.tuttifruttia.domain.scoring.PointsRule;
import com.example.tuttifruttia.domain.scoring.ScoreCalculator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

public class LetterStratTest {

    private  UUID id;
    private GameState state;
    private ScoreBoard scoreBoard;
    private Round currentRound;

    public GameState getState() {
        return state;
    }


    public Round getCurrentRound() {
        return currentRound;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    private HybridJudge newHybridJudge() {
        // 1) Cliente real de Groq, leyendo GROQ_API_KEY del entorno
        LLMProvider provider = Groq.fromEnv();

        // 2) SemanticJudge real (usa Groq)
        SemanticJudge semanticJudge = new SemanticJudge(provider);

        // 3) Regla básica simple (mínimo 3 caracteres, letra correcta, etc.)
        RuleBasedJudge ruleBasedJudge = new RuleBasedJudge(3);

        // 4) HybridJudge que combina ambos
        return new HybridJudge(ruleBasedJudge, semanticJudge);
    }

    @Test
    void nextLetterTest(){

        HybridJudge hybridJudge = newHybridJudge();

        LetterStrategy letterStrategy = new UniformLetterStrategy();

        List<Character> letras = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<Category> categories = new ArrayList<>();

        PointsRule pointsRule = new PointsRule(10,5,0);

        for (char c = 'A'; c <= 'Z'; c++) {
            letras.add(c);
        }

        Alphabet alphabet = new Alphabet(letras);

        UUID id = UUID.randomUUID();

        Category cat1 = new Category(id,"Paises",true);

        categories.add(cat1);

        GameSettings settings = new GameSettings(categories,10,pointsRule,alphabet);

        Letter letter = letterStrategy.nextLetter(settings.getAlphabet());

        // Creamos una nueva Round y la arrancamos
        Round round = new Round();
        round.start(letter, settings.getRoundSeconds());

        this.currentRound = round;

        System.out.println(round.getLetter());

        Answer answer = new Answer("Jamaica",true,a);

        AnswerSet answerSet = new AnswerSet();
        answerSet.put(cat1,answer);

        currentRound.setAnswers(answerSet);

        Map<Category, ValidationResult> results = new HashMap<>();
        for (Category category : settings.getCategories()) {
            Answer respuesta = answerSet.get(category); // puede ser null si no respondió
            String text = respuesta != null ? respuesta.getText() : null;

            ValidationResult vr = hybridJudge.validate(
                    currentRound.getLetter(),
                    category,
                    text
            );
            results.put(category, vr);

            System.out.println(">>> Resultado : "
                    + vr.getStatus() + " | " + vr.getReasons());
        }



    }

}
