package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.repository.AdopcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdopcionService {

    @Autowired
    private AdopcionRepository adopcionRepository;

    public long contarTodos() {
        return adopcionRepository.count();
    }
}
