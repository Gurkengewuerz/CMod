package de.gurkengewuerz.cmod.manager;

/**
 *
 * @author gurkengewuerz.de
 */
public enum Flags {

    PROTECTION, WELCOME, FAREWELL, PVP, CREEPER, TNT, EXPLOSION, FIRE, RESTRICTION, INTERACT, GODMODE;

    private static int ids;
    private int id;

    static {
        ids = 0;
        Flags[] arrayOfFlags;
        int j = (arrayOfFlags = values()).length;
        for (int i = 0; i < j; i++) {
            Flags flag = arrayOfFlags[i];
            flag.id = (ids++);
        }
    }

    public String getName() {
        switch (this) {
            case PROTECTION:
                return "Protection";

            case WELCOME:
                return "Willkommens Nachricht";

            case FAREWELL:
                return "Verabschiedungs Nachricht";

            case PVP:
                return "PVP";

            case CREEPER:
                return "Creeper Explosionen";

            case TNT:
                return "TNT Explosionen";

            case EXPLOSION:
                return "Explosionen";

            case FIRE:
                return "Feuer";

            case RESTRICTION:
                return "Restriction";

            case INTERACT:
                return "Interagieren";

            case GODMODE:
                return "Godmodus";
                
            default:
                return "Unbekannte Flag";
        }
    }

    public String toString() {
        return super.toString().toLowerCase();
    }

    public Integer getId() {
        return this.id;
    }
}
