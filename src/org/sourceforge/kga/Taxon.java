package org.sourceforge.kga;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.*;

import org.sourceforge.kga.gui.gardenplan.GardenView;

public class Taxon <T extends Taxon> implements Comparable<Taxon>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    @Override
    public int compareTo(Taxon o)
    {
        return id-o.id;
    }

    // constants
    public enum Type { KINGDOM, FAMILY, GENUS, SPECIES, SUBSPECIES, ITEM };

    // specific properties
    private int id;
    private Type type;
    // TODO: rename this to taxon
    private String name = "unknown";
    private Map<String, String> translations;
    private Image image;
    String imageString = null;
    protected Taxon parent = null;
    private Set<Taxon> children = new TreeSet<>(
            new Comparator<Taxon>()
            {
                @Override
                public int compare(Taxon p1, Taxon p2)
                {
                    return p1.id - p2.id;
                }
            });

    public Taxon(int id)
    {
        this.id = id;
    }

    public Taxon(Type type, int id, String name, Taxon parent)
    {
        String[] nameParts = name.replace("×", "").replace("  ", " ").split(" ");
        int nameSize = nameParts.length;
        if (type != Type.KINGDOM && parent == null)
            log.severe("Parent not specified for " + name);
        else if (type == Type.FAMILY && parent.type != Type.KINGDOM ||
                type == Type.GENUS && parent.type != Type.FAMILY ||
                type == Type.SPECIES && parent.type != Type.FAMILY && parent.type != Type.GENUS ||
                type == Type.SUBSPECIES && parent.type != Type.SPECIES)
        {
            log.severe("Invalid parent type " + parent.type.toString() + " for " + type.toString() + " " + name);
        }
        else if (nameSize != 1 && (type == Type.KINGDOM || type == Type.FAMILY || type == Type.GENUS) ||
                nameSize != 2 && type == Type.SPECIES || nameSize != 4 && type == Type.SUBSPECIES)
        {
            log.severe("Invalid name " + name + " for " + type.toString());
        }
        else if (type == Type.SPECIES && parent.type == Type.GENUS && !name.startsWith(parent.name + " "))
        {
            log.severe("Invalid species name " + name + " in genus " + parent.name);
        }
        else if (type == Type.SUBSPECIES && !name.startsWith(parent.name + " "))
        {
            log.severe("Invalid subspecies name " + name + " in species " + parent.name);
        }
        else if (type == Type.SUBSPECIES &&
                nameParts[2].compareTo("subsp.") != 0 &&
                nameParts[2].compareTo("var.") != 0 &&
                nameParts[2].compareTo("subvar.") != 0 &&
                nameParts[2].compareTo("f.") != 0 &&
                nameParts[2].compareTo("subf.") != 0)
        {
            log.severe("Invalid connector name " + nameParts[2] + " for " + name);
        }
        this.id = id;
        this.type = type;
        this.name = name.trim();
        this.parent = parent;
        this.translations = new HashMap<String, String>();
        if (parent != null)
            this.parent.children.add(this);
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Plant)
            return ((Plant) obj).getId() == this.getId();
        else
            return false;
    }

    @Override
    public String toString()
    {
        return "Plant " + getName() + " id: " + getId();
    }

    // specific getters
    public Type     getType()                       { return type; }
    public int      getId()                         { return id; }
    public String   getName()                       { return name; }
    public String   getTranslation(String language) { return translations.get(language); }
    public boolean  isItem()                        { return type == Type.ITEM; } // TODO: refactory this
    public Image    getImage()                      { return image; }
    
    public ImageView createImageview(int maxSize) {
    	Image image = this.image;
    	Set<Taxon> children = this.children;
    	while (image == null && children.size()==1) {
    		image = children.iterator().next().image;
    		children = children.iterator().next().children;
    	}
    	return createImageview(image,maxSize);
    }
    
    public static ImageView  createImageview(Image image, int maxSize) {
    	 ImageView imageView = new ImageView();
         imageView.setImage(image);
         imageView.setPreserveRatio(true);
         if(image!=null) {
        	 if(image.getHeight()>image.getWidth()) {
        		 imageView.setFitHeight(maxSize);
        	 }
        	 else {
        		 imageView.setFitWidth(maxSize);            	
        	 }
         }else{
    		 imageView.setFitHeight(maxSize);
    		 imageView.setFitWidth(maxSize);             	
         }
         imageView.setSmooth(true);
         imageView.setCache(true);
         return imageView;
    }
    
    
    public Taxon    getFamily()
    {
        Taxon p = parent;
        while (p != null && p.type != Type.FAMILY)
        {
            p = p.parent;
        }
        return p;
    }

    public Taxon getTaxonParent()   { return parent; }
    public Set<Taxon> getChildren() { return children; }

    public boolean isParentOf(T child)
    {
        Taxon found = child.parent;
        while (found != null)
        {
            if (found == this)
                return true;
            found = found.parent;
        }
        return false;
    }

    // translations
    public void setTranslation(String language, String value)
    {
        translations.put(language, value);
    }

    public Map<String, String> getTranslations()
    {
        return translations;
    }

    // image
    public void setImage(String buffer)
    {
        // log.fine("setting image for species " + Integer.toString(id));
        byte imageBytes[] = java.util.Base64.getDecoder().decode(buffer.replaceAll("\n", ""));
        image = new Image(new ByteArrayInputStream(imageBytes));
        imageString = buffer;
    }

    public String getImageAsString()
    {
        return imageString;
    }
}
