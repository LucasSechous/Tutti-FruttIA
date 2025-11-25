package com.example.tuttifruttia.controller;

import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.letter.Alphabet;
import com.example.tuttifruttia.domain.letter.LetterStrategy;
import com.example.tuttifruttia.domain.letter.UniformLetterStrategy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class pruebaLetra {

    @GetMapping("/api")
    public Letter obetenerLetra(){

        List<Character> lista = List.of('A', 'B', 'C', 'D');

        Alphabet alphabet = new Alphabet(lista);

        LetterStrategy letter = new UniformLetterStrategy();

        return letter.nextLetter(alphabet);
    }
}
