package org.vchoi.unyleya.tcc;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Lógica de negócio relacionada ao racionamento
 *
 * Localização -> código de racionamento
 * Código de racionamento -> escala de racionamento
 */

public class RacionamentoUtil {

    public static final String ESTADO_ESTABILIZADO = "Estabilizado";
    public static final String ESTADO_INTERROMPIDO = "Interrompido";
    public static final String ESTADO_EM_ESTABILIZACAO = "Em estabilização";
    public static final String ESTADO_DESCONHECIDO = "Desconhecido";

    // Sistema Torto/Santa Maria
    public static final String AREA_SM1 = "SM 01";
    public static final String AREA_SM2 = "SM 02";
    public static final String AREA_SM3 = "SM 03";
    public static final String AREA_SM4 = "SM 04";
    public static final String AREA_SM5 = "SM 05";
    public static final String AREA_SM6 = "SM 06";
    public static final String AREA_SM7 = "SM 07";
    public static final String AREA_SM8 = "SM 08";
    public static final String AREA_SM9 = "SM 09";
    public static final String AREA_SM10 = "SM 10";
    public static final String AREA_SM11 = "SM 11";

    // Sistema Descoberto
    public static final String AREA_RD1A = "RD 01a";
    public static final String AREA_RD1B = "RD 01b";
    public static final String AREA_RD2 = "RD 02";
    public static final String AREA_RD3 = "RD 03";
    public static final String AREA_RD4 = "RD 04";
    public static final String AREA_RD5 = "RD 05";
    public static final String AREA_RD6 = "RD 06";
    public static final String AREA_RD7 = "RD 07";
    public static final String AREA_RD8 = "RD 08";
    public static final String AREA_RD9 = "RD 09";
    public static final String AREA_RD10 = "RD 10";
    public static final String AREA_RD11 = "RD 11";

    // Sistemas isolados, pequenas captações
    public static final String AREA_SS1 = "SS 01";
    public static final String AREA_SS2 = "SS 02";
    public static final String AREA_SS3 = "SS 03";
    public static final String AREA_SS4 = "SS 04";
    public static final String AREA_SS5 = "SS 05";
    public static final String AREA_SS6 = "SS 06";
    
    public static final String[] AREAS_RACIONAMENTO = {
            AREA_SM1, AREA_SM2, AREA_SM3, AREA_SM4, AREA_SM5, AREA_SM6, AREA_SM7, AREA_SM8,
            AREA_SM9, AREA_SM10, AREA_SM11,

            AREA_RD1A, AREA_RD1B, AREA_RD2, AREA_RD3, AREA_RD4, AREA_RD5, AREA_RD6, AREA_RD7,
            AREA_RD8, AREA_RD9, AREA_RD10, AREA_RD11,

            AREA_SS1, AREA_SS2, AREA_SS3, AREA_SS4, AREA_SS5, AREA_SS6
    };

    private Bundle areasRacionamento;

    public RacionamentoUtil() {
        createPolylineBundle();
    }

    private void createPolylineBundle() {
        /*
        Cria um bundle com as áreas de racionamento, associando-os aos códigos.

        As chaves incluem o código da área + uma letra serial para áreas geograficamente desconexas
         */

        // Encoded Polylines
        // ref: https://developers.google.com/maps/documentation/utilities/polylineutility
        areasRacionamento = new Bundle();

        // Perímetro do DF, somente para testes
//        areasRacionamento.putString("DF",
//                "lvq}Atlk`Ht_OsmT|bb@dPjl\\xiKjc\\mrKkZ~q~D_zj@|i@mz`A}lPoOc`oC");

        // SM 1, a = Lago Norte, Varjão, SMLN, Granja do Torto
        areasRacionamento.putString(AREA_SM1 + ",1",
                "lc__B|nicHcmBdiDwkBc}AnmAi|Asq@yeAjhBcjAde@{rBvsDa}ErrAoc@fg@tWdeBscAfFurAtTlEcCb|@yx@hmAqp@zh@dFn_BwuEraGut@zi@iH|x@");

        // SM 1, b = SMU
        areasRacionamento.putString(AREA_SM1 + ",2",
                "n`i_BjxncHiiBg`@caAvqBtSpwDz{@zfAddAkS~]qfEcc@oUrRkmA");

        // SM 3, a = Asa Norte
        areasRacionamento.putString(AREA_SM3 + ",1",
                "tpk_B|`|bHeo@zOiJjfDywAi~@sqDthEom@xh@kKb{@nn@pkCllBh_Anm@wdAdeBh`@jjA{hF`j@kmC~Us~@yx@u~B");

        // SM 6, a = Asa Sul
        areasRacionamento.putString(AREA_SM6 + ",1",
                "fwm_Bpn|bHfrBj@eEvoHhjBzaCny@|mBacChhCgfFwuFjkAwoHqO_dC");
    }

    public String getCodigoRacionamento(Location location) {

        LatLng latLng = null;

        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        return getCodigoRacionamento(latLng);
    }

    public String getCodigoRacionamento(LatLng latLng) {

        List<LatLng> poly;
        boolean geodesic = false;
        boolean isInside;

        if (latLng != null) {
            for (String k : areasRacionamento.keySet()) {
                poly = PolyUtil.decode(areasRacionamento.getString(k));
                isInside = PolyUtil.containsLocation(latLng, poly, geodesic);
                if (isInside) {
                    String[] codigo = k.split(",");
                    isInside = false;
                    return codigo[0];
                }
            }
        }
        return "?";
    }

    public String getEstadoAbastecimento(Location location) {
        return getEstadoAbastecimento(getCodigoRacionamento(location));
    }

    public String getEstadoAbastecimento(String codRacionamento) {

        Bundle diasIniciais = new Bundle();

        diasIniciais.putString(AREA_SM1, "05/01/2018");
        diasIniciais.putString(AREA_SM2, "05/01/2018");
        diasIniciais.putString(AREA_SM3, "06/01/2018");
        diasIniciais.putString(AREA_SM4, "07/01/2018");
        diasIniciais.putString(AREA_SM5, "07/01/2018");
        diasIniciais.putString(AREA_SM6, "02/01/2018");
        diasIniciais.putString(AREA_SM7, "03/01/2018");
        diasIniciais.putString(AREA_SM8, "04/01/2018");
        diasIniciais.putString(AREA_SM9, "03/01/2018");
        diasIniciais.putString(AREA_SM10, "04/01/2018");
        diasIniciais.putString(AREA_SM11, "02/01/2018");

        diasIniciais.putString(AREA_RD1A, "05/01/2018");
        diasIniciais.putString(AREA_RD1B, "02/01/2018");
        diasIniciais.putString(AREA_RD2, "04/01/2018");
        diasIniciais.putString(AREA_RD3, "06/01/2018");
        diasIniciais.putString(AREA_RD4, "03/01/2018");
        diasIniciais.putString(AREA_RD5, "02/01/2018");
        diasIniciais.putString(AREA_RD6, "03/01/2018");
        diasIniciais.putString(AREA_RD7, "02/01/2018");
        diasIniciais.putString(AREA_RD8, "05/01/2018");
        diasIniciais.putString(AREA_RD9, "02/01/2018");
        diasIniciais.putString(AREA_RD10, "01/01/2018");
        diasIniciais.putString(AREA_RD11, "06/01/2018");

        diasIniciais.putString(AREA_SS1, "02/01/2018");
        diasIniciais.putString(AREA_SS2, "03/01/2018");
        diasIniciais.putString(AREA_SS3, "04/01/2018");
        diasIniciais.putString(AREA_SS4, "05/01/2018");
        diasIniciais.putString(AREA_SS5, "06/01/2018");
        diasIniciais.putString(AREA_SS6, "07/01/2018");

        for (int i = 0; i < AREAS_RACIONAMENTO.length - 1; i++) {
            if (!AREAS_RACIONAMENTO[i].equalsIgnoreCase(codRacionamento)) {
                continue;
            }

            return getEstadoRacionamento6d(
                    diasIniciais.getString(AREAS_RACIONAMENTO[i])
                );
        }

        return "Área desconhecida";
    }

    private String getEstadoRacionamento6d(String dataBase) {

        SimpleDateFormat formatoBrasileiro = new SimpleDateFormat("dd/MM/yyyy");
        Date dataInicial, hoje;

        hoje = new Date();

        try {
            dataInicial = formatoBrasileiro.parse(dataBase);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao converter a data base do racionamento";
        }

        long diff = hoje.getTime() - dataInicial.getTime();
        long days = (diff / (1000*60*60*24));
        int ciclo = (int) days % 6;

        // ciclo de 6d: IMMEEE
        //              012345
        switch (ciclo) {
            case 0:
                return ESTADO_INTERROMPIDO;
            case 1:
            case 2:
                return ESTADO_EM_ESTABILIZACAO;
            default:
                return ESTADO_ESTABILIZADO;
        }
    }
}
