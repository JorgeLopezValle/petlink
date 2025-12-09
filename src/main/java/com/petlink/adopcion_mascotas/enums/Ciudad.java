package com.petlink.adopcion_mascotas.enums;

public enum Ciudad {
    A_CORUNA("A Coruña"),
    ALBACETE("Albacete"),
    ALICANTE("Alicante"),
    ALMERIA("Almería"),
    ARRECIFE("Arrecife (Lanzarote)"),
    AVILA("Ávila"),
    BADAJOZ("Badajoz"),
    BARCELONA("Barcelona"),
    BILBAO("Bilbao"),
    BURGOS("Burgos"),
    CACERES("Cáceres"),
    CADIZ("Cádiz"),
    CASTELLON_DE_LA_PLANA("Castellón de la Plana"),
    CEUTA("Ceuta"),
    CIUDAD_REAL("Ciudad Real"),
    CORDOBA("Córdoba"),
    CUENCA("Cuenca"),
    DONOSTIA_SAN_SEBASTIAN("Donostia-San Sebastián"),
    ELCHE("Elche"),
    GIJON("Gijón"),
    GIRONA("Girona"),
    GRANADA("Granada"),
    GUADALAJARA("Guadalajara"),
    HUELVA("Huelva"),
    HUESCA("Huesca"),
    IBIZA("Ibiza"),
    JAEN("Jaén"),
    JEREZ_DE_LA_FRONTERA("Jerez de la Frontera"),
    LAS_PALMAS_DE_GRAN_CANARIA("Las Palmas de Gran Canaria"),
    LEON("León"),
    LLEIDA("Lleida"),
    LOGRONO("Logroño"),
    LUGO("Lugo"),
    MADRID("Madrid"),
    MALAGA("Málaga"),
    MELILLA("Melilla"),
    MERIDA("Mérida"),
    MURCIA("Murcia"),
    OURENSE("Ourense"),
    OVIEDO("Oviedo"),
    PALENCIA("Palencia"),
    PALMA_DE_MALLORCA("Palma de Mallorca"),
    PAMPLONA("Pamplona"),
    PONTEVEDRA("Pontevedra"),
    PUERTO_DEL_ROSARIO("Puerto del Rosario (Fuerteventura)"),
    SALAMANCA("Salamanca"),
    SAN_CRISTOBAL_DE_LA_LAGUNA("San Cristóbal de La Laguna"),
    SANTA_CRUZ_DE_TENERIFE("Santa Cruz de Tenerife"),
    SANTANDER("Santander"),
    SANTIAGO_DE_COMPOSTELA("Santiago de Compostela"),
    SEGOVIA("Segovia"),
    SEVILLA("Sevilla"),
    SORIA("Soria"),
    TARRAGONA("Tarragona"),
    TELDE("Telde"),
    TERUEL("Teruel"),
    TOLEDO("Toledo"),
    VALENCIA("Valencia"),
    VALLADOLID("Valladolid"),
    VIGO("Vigo"),
    VITORIA_GASTEIZ("Vitoria-Gasteiz"),
    ZAMORA("Zamora"),
    ZARAGOZA("Zaragoza");

    private final String nombre;

    Ciudad(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
