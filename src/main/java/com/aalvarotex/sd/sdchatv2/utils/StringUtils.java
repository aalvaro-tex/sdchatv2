/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author alvar
 */
@Named
@ManagedBean
@SessionScoped
public class StringUtils  implements Serializable {
    private static final DateTimeFormatter IN_FMT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss")
                             .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter OUT_TIME =
            DateTimeFormatter.ofPattern("HH:mm");
    /**
     * @param textoFecha fecha/hora en formato "dd-MM-yyyy HH:mm:ss" (hora local)
     * @return si es hoy -> "HH:mm"; si es de un día anterior -> número de días transcurridos.
     *         (Si la fecha es futura, devuelve "0" días.)
     */
    public static String horaOHaceDias(String textoFecha) {
        return horaOHaceDias(textoFecha, ZoneId.systemDefault());
    }

    /** Variante con zona explícita (por ejemplo ZoneId.of("Europe/Madrid"))
     * @param textoFecha
     * @param zone
     * @return  */
    public static String horaOHaceDias(String textoFecha, ZoneId zone) {
        LocalDateTime ldt = LocalDateTime.parse(textoFecha, IN_FMT);
        LocalDate fecha = ldt.toLocalDate();
        LocalDate hoy = LocalDate.now(zone);

        if (fecha.isEqual(hoy)) {
            return ldt.format(OUT_TIME);        // mismo día -> "HH:mm"
        }
        long dias = ChronoUnit.DAYS.between(fecha, hoy);
        return String.valueOf(Math.max(dias, 0)); // si es futura, devuelve "0"
    }

}
