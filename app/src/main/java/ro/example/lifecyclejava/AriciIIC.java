package ro.example.lifecyclejava;

import android.os.Parcel;
import android.os.Parcelable;

/** Modelul unui arici, cu inițialele I.I.C. în numele clasei. */
public class AriciIIC implements Parcelable {
    private String nume;
    private int varstaAni;
    private String zona;
    private String sex;
    private boolean areAdapostSigur;
    private float nivelPrietenie;
    private boolean activNoaptea;

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

    private AriciIIC(Parcel in) {
        nume = in.readString();
        varstaAni = in.readInt();
        zona = in.readString();
        sex = in.readString();
        areAdapostSigur = in.readByte() != 0;
        nivelPrietenie = in.readFloat();
        activNoaptea = in.readByte() != 0;
    }

    public static final Creator<AriciIIC> CREATOR = new Creator<AriciIIC>() {
        @Override
        public AriciIIC createFromParcel(Parcel in) {
            return new AriciIIC(in);
        }

        @Override
        public AriciIIC[] newArray(int size) {
            return new AriciIIC[size];
        }
    };

    public String getNume() {
        return nume;
    }

    public int getVarstaAni() {
        return varstaAni;
    }

    public String getZona() {
        return zona;
    }

    public String getSex() {
        return sex;
    }

    public boolean hasAdapostSigur() {
        return areAdapostSigur;
    }

    public float getNivelPrietenie() {
        return nivelPrietenie;
    }

    public boolean isActivNoaptea() {
        return activNoaptea;
    }

    public void updateFrom(AriciIIC updatedArici) {
        nume = updatedArici.nume;
        varstaAni = updatedArici.varstaAni;
        zona = updatedArici.zona;
        sex = updatedArici.sex;
        areAdapostSigur = updatedArici.areAdapostSigur;
        nivelPrietenie = updatedArici.nivelPrietenie;
        activNoaptea = updatedArici.activNoaptea;
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
        return varstaAni + " ani  ·  " + zona + "  ·  " + sex
                + "\nAdăpost: " + (areAdapostSigur ? "sigur" : "nesigur")
                + "  ·  Prietenie: " + nivelPrietenie + "/5"
                + "  ·  Activ noaptea: " + (activNoaptea ? "da" : "nu");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nume);
        dest.writeInt(varstaAni);
        dest.writeString(zona);
        dest.writeString(sex);
        dest.writeByte((byte) (areAdapostSigur ? 1 : 0));
        dest.writeFloat(nivelPrietenie);
        dest.writeByte((byte) (activNoaptea ? 1 : 0));
    }
}
