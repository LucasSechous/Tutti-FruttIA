package com.example.tuttifruttia.domain.letter;

import com.example.tuttifruttia.domain.core.Letter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UniformLetterStrategy implements LetterStrategy {

    @Override
    public Letter nextLetter(Alphabet alphabet){

        // 1. Obtener el set de letras
        Set<Character> letrasSet = alphabet.getLetters();

        // 2. Convertir a lista (porque el Set no tiene índices)
        List<Character> letrasList = new ArrayList<>(letrasSet);

        // 3. Elegir un índice al azar
        int index = ThreadLocalRandom.current().nextInt(letrasList.size());

        // 4. Obtener la letra sorteada
        char letraSorteada = letrasList.get(index);

        // 5. Construir y devolver el objeto Letter
        return new Letter(letraSorteada);
    }

}
