package ro.example.lifecyclejava;

import java.io.Serializable;

/** Modelul unui arici, cu inițialele I.I.C. în numele clasei. */
public class AriciIIC implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nume;
    private final int varstaAni;
    private final String zona;
    private final String sex;
    private final boolean areAdapostSigur;
    private final float nivelPrietenie;
    private final boolean activNoaptea;

    public AriciIIC(String nume, int varstaAni, String zona, String sex,
                    boolean areAdapostSigur, float nivelPrietenie, boolean activNoaptea) {
        this.nume = nume;
        this.varstaAni = varstaAni;
        this.zona = zona;
        this.sex = sex;
        this.areAdapostSigur = areAdapostSigur;
        this.nivelPrietenie = nivelPrietenie;
        this.activNoaptea = activNoaptea;
    }

    public String toDisplayText() {
        return "🦔 " + nume
                + "\nA · " + varstaAni + " ani    •    Z · " + zona
                + "\n" + sex
                + "\nAdăpost sigur: " + (areAdapostSigur ? "Da" : "Nu")
                + "\nPrietenie: " + nivelPrietenie + "/5"
                + "\nActiv noaptea: " + (activNoaptea ? "Da" : "Nu");
    }

    @Override
    public String toString() {
        return "🦔 " + nume
                + "  ·  " + varstaAni + " ani"
                + "  ·  " + zona
                + "  ·  " + sex
                + "  ·  adăpost: " + (areAdapostSigur ? "da" : "nu")
                + "  ·  prietenie: " + nivelPrietenie + "/5"
                + "  ·  activ noaptea: " + (activNoaptea ? "da" : "nu");
    }
}
