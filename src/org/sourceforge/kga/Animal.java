package org.sourceforge.kga;

import org.sourceforge.kga.Taxon.Type;

public class Animal extends Taxon<Animal>
{
    private static Animal animalia = null;
    public static Animal getKingdom()
    {
        if (animalia == null)
            animalia = new Animal(Type.KINGDOM, 100000, "Animal", null);
        return animalia;
    }

    public Animal(int id)
    {
        super(id);
    }

    public Animal(Type type, int id, String name, Taxon parent)
    {
        super(type, id, name, parent);
    }
}
