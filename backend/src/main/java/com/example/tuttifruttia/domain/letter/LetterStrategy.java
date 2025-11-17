package com.example.tuttifruttia.domain.letter;

import com.example.tuttifruttia.domain.core.Letter;

public interface LetterStrategy {


    Letter nextLetter(Alphabet alphabet);
}
