package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping("/")
    public String home(ModelMap model) {
        List<Mascota> todasMascotas = mascotaService.r();

        Map<String, List<Mascota>> mascotasPorEspecie = todasMascotas.stream()
            .collect(Collectors.groupingBy(
                Mascota::getEspecie,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparing(Mascota::isAdoptada))
                        .collect(Collectors.toList())
                )
            ));

        Map<String, List<Mascota>> mascotasPorEspecieOrdenado = mascotasPorEspecie.entrySet()
            .stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        model.put("mascotasPorEspecie", mascotasPorEspecieOrdenado);
        model.put("view", "home/home");
        return "_t/frame";
    }
}