package org.sourceforge.kga.gui.actions.importData;

import java.io.*;
import java.util.*;

import org.sourceforge.kga.*;
import org.sourceforge.kga.plant.*;

public class CsvParser
{
    private class ParseException extends Exception
    {
        public ParseException(int column, String message)
        {
            super(message);
            this.column = column;
            this.message = message;
        }

        int column;
        String message;

        public int getColumn()
        {
            return column;
        }
    }

    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    ArrayList<PropertySource> sources = new ArrayList<>();
    TreeMap<String, ArrayList<String>> tags = new TreeMap<>();
    ArrayList<String> columns = new ArrayList<>();
    int pageColumn, nameColumn, companionColumn, rootColumn, nutritionalColumn, weedColumn;
    ArrayList<List<String>> values = new ArrayList<>();
    TreeMap<Integer, TreeMap<Integer, String>> errors = new TreeMap<>();

    SourceTableModel sourceModel;
    TagTableModel tagModel;
    TaggedPlantsTableModel taggedPlantsModel;
    DataTableModel dataModel;

    public CsvParser()
    {
    }

    public void loadFile(File file) throws Exception
    {
        sources.clear();
        columns.clear();
        values.clear();

        BufferedReader reader = null;
        try
        {
            log.info("importing");
            reader = new BufferedReader(new FileReader(file));
            String[] splitLine = readAndSplitLine(reader);

            // sources
            while (splitLine[0].compareTo("source") == 0)
            {
                PropertySource source = new PropertySource(
                        sources.size() + 1, splitLine[1], splitLine.length == 2 ? "" : splitLine[2]);
                sources.add(source);

                splitLine = readAndSplitLine(reader);
            }

            // tags
            while (splitLine[0].compareTo("tag") == 0)
            {
                log.finest("Tag parsed " + splitLine[1]);
                List<String> plants = Arrays.asList(splitLine).subList(2, splitLine.length);
                tags.put(splitLine[1], new ArrayList<>(plants));

                splitLine = readAndSplitLine(reader);
            }

            columns.addAll(Arrays.asList(splitLine));

            pageColumn = -1;
            companionColumn = -1;
            rootColumn = -1;
            nutritionalColumn = -1;
            weedColumn = -1;

            nameColumn = 0;
            if (sources.size() > 1)
            {
                if (columns.get(nameColumn).compareTo("ref") != 0)
                    throw new Exception("First column must be ref; found " + columns.get(nameColumn));
                ++nameColumn;
            }
            if (columns.get(nameColumn).compareTo("page") == 0)
            {
                pageColumn = nameColumn;
                ++nameColumn;
            }
            if (columns.get(nameColumn).compareTo("name") != 0)
                throw new Exception("First column must be name; found " + columns.get(nameColumn));
            for (int i = nameColumn + 1; i < columns.size(); ++i)
                if (columns.get(i).compareTo("companion_type") == 0)
                {
                    int s;
                    if (columns.get(i + 1).compareTo("companion_name") != 0)
                        throw new Exception("Expected column: companion_name; found: " + columns.get(i + 1));
                    if (columns.get(i + 2).compareTo("companion_effect") == 0)
                        s = 0;
                    else if (columns.get(i + 2).compareTo("companion_scientific") != 0)
                        throw new Exception("Expected column: companion_scientific or companion_effect; found: " + columns.get(i + 2));
                    else
                        s = 1;
                    if (columns.get(i + 3 + s).compareTo("companion_info") != 0)
                        throw new Exception("Expected column: companion_info; found: " + columns.get(i + 3 + s));
                    companionColumn = i;
                }
                else if (columns.get(i).compareTo("root") == 0)
                {
                    rootColumn = i;
                }
                else if (columns.get(i).compareTo("nutritional") == 0)
                {
                    nutritionalColumn = i;
                }
                else if (columns.get(i).compareTo("weed") == 0)
                {
                    weedColumn = i;
                }

            int row = 0;
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.startsWith("#"))
                {
                    /* ArrayList<String> comment = new ArrayList<>();
                    comment.add(line);
                    values.add(comment); */
                    continue;
                }

                ArrayList<String> lineValues = new ArrayList<>();
                lineValues.addAll(Arrays.asList(line.split("[\t]")));
                if (lineValues.size() > columns.size())
                    throw new Exception("Too many values on row: " + Integer.toString(row) + "\n" + line);
                while (lineValues.size() < columns.size())
                    lineValues.add("");
                values.add(lineValues);

                for (int i = 0; i < columns.size(); ++ i)
                    checkForErrors(row, i);
                ++row;
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            catch (Exception ex) {}
        }

        sourceModel = new SourceTableModel(this);
        tagModel = new TagTableModel(this);
        taggedPlantsModel = new TaggedPlantsTableModel(this);
        dataModel = new DataTableModel(this);
    }

    static private Plant findByName(String name, boolean scientific)
    {
        if (name == null || name.isEmpty())
            return null;
        for (Plant plant : Resources.plantList().getPlants())
        {
            if (scientific)
            {
                if (plant.getName().compareTo(name) == 0)
                    return plant;
            }
            else
            {
                String en = plant.getTranslation("en");
                if (en == null || en.isEmpty())
                    continue;
                if (en.compareTo(name) == 0)
                    return plant;
            }
        }
        return null;
    }

    private static Plant findPlant(String name)
    {
        log.fine("find plant " + name);
        Plant found = findByName(name, false);
        if (found != null)
            return found;
        return findByName(name, true);
    }

    static private Animal findAnimalByName(String name, boolean scientific)
    {
        if (name == null || name.isEmpty())
            return null;
        for (Animal animal : Resources.plantList().getAnimals())
        {
            if (scientific)
            {
                if (animal.getName().compareTo(name) == 0)
                    return animal;
            }
            else
            {
                String en = animal.getTranslation("en");
                if (en == null || en.isEmpty())
                    continue;
                if (en.compareTo(name) == 0)
                    return animal;
            }
        }
        return null;
    }

    private static Animal findAnimal(String name)
    {
        log.fine("find animal " + name);
        Animal found = findAnimalByName(name, false);
        if (found != null)
            return found;
        return findAnimalByName(name, true);
    }

    private static Plant findPlant(String name, String scientific) throws Exception
    {
        log.fine("find plant " + name + " " + scientific);

        Plant foundByName = findByName(name, false);
        Plant foundByScientific = findByName(scientific, true);
        if (foundByName != null && foundByScientific != null && foundByName != foundByScientific)
        {
            throw new Exception("Found different plants for " + name + " " + scientific);
        }
        if (foundByName != null && !scientific.isEmpty() && foundByName.getName().compareTo(scientific) != 0)
        {
            throw new Exception("Invalid scientific name for " + name + ": " + scientific);
        }
        if (foundByScientific != null && !name.isEmpty())
        {
            String foundName = foundByScientific.getTranslation("en");
            if (foundName == null || foundName.compareTo(name) != 0)
                throw new Exception("Invalid name for " + scientific + ": " + name);
        }
        return foundByName != null ? foundByName : foundByScientific;
    }

    private Set<Plant> findTaggedPlants(String name)
    {
        // now search in tags
        ArrayList<String> p = tags.get(name);
        if (p == null)
            return null;

        log.fine("Parsing tag " + name);
        Set<Plant> found = new HashSet<>();
        for (String s : p)
        {
            log.fine("Parsing plant in tag " + s);
            Plant plant = null;
            try { plant = findPlant(s, ""); }
            catch (Exception ex) {}
            if (plant == null)
            {
                try { plant = findPlant("", s); }
                catch (Exception ex) {}
            }

            if (plant != null)
            {
                log.fine("Plant with tag found ");
                found.add(plant);
            }
            else
            {
                log.fine("Plant with tag not found ");
            }
        }
        return found;
    }

    public void checkForErrors(int i, int column)
    {
        List<String> row = values.get(i);
        TreeMap<Integer, String> error = errors.get(i);

        try
        {
            while (column >= 0)
            {
                if (error != null)
                    error.remove(column);
                if (column == companionColumn)
                    parseColumnCompanion(row);
                else if (column == rootColumn)
                    parseColumnRoot(row);
                else if (column == nutritionalColumn)
                    parseColumnNutritional(row);
                else if (column == weedColumn)
                    parseColumnWeedControl(row);
                else if (column == nameColumn)
                    parseColumnPlant(row, nameColumn, columns.get(nameColumn + 1).compareTo("scientific") == 0);
                else if (column == 0)
                    parseColumnReference(row);
                else
                {
                    --column;
                    continue;
                }
                break;
            }
        }
        catch (ParseException ex)
        {
            if (error == null)
            {
                error = new TreeMap<>();
                errors.put(i, error);
            }
            error.put(ex.getColumn(), ex.getMessage());
        }
    }

    private String[] readAndSplitLine(BufferedReader reader) throws Exception
    {
        String line = reader.readLine();
        if (line == null)
            throw new Exception("Can not read line");
        return line.split("[\t]");
    }

    Reference parseColumnReference(List<String> row)
    {
        int id = 0;
        PropertySource source;
        String page = null;
        if (columns.get(0).compareTo("ref") == 0)
            id = Integer.parseInt(row.get(0)) - 1;
        source = sources.get(id);
        source = Resources.plantList().addSource(source.name, source.url);
        if (pageColumn != -1 && !row.get(pageColumn).isEmpty())
            page = row.get(pageColumn);
        return new Reference(source, page);
    }

    Set<Plant> parseColumnPlant(List<String> row, int columnName, boolean withScientific) throws ParseException
    {
        String name = row.get(columnName);

        Plant found;
        if (withScientific)
            try { found = findPlant(name, row.get(columnName + 1)); }
            catch (Exception ex) { throw new ParseException(columnName, ex.getMessage()); }
        else
            found = findPlant(name);
        if (found != null)
        {
            Set<Plant> result = new HashSet<>();
            result.add(found);
            return result;
        }

        Set<Plant> foundTagged = findTaggedPlants(name);
        if (foundTagged != null)
            return foundTagged;

        if (withScientific && !row.get(columnName + 1).isEmpty())
            throw new ParseException(columnName, "Can not find plant " + name + " ( " + row.get(columnName + 1) + " )");
        else
            throw new ParseException(columnName, "Can not find plant " + name);
    }

    class ParsedCompanion
    {
        public Companion.Type type;
        public Set<Plant> companions;
        public boolean eachOther;
        public boolean addToCompanions;
        public TreeSet<Animal> animals = new TreeSet<>();
        public TreeSet<Companion.Improve> improve = new TreeSet<>();
    }

    ParsedCompanion parseColumnCompanion(List<String> row) throws ParseException
    {
        ParsedCompanion parsedCompanion = new ParsedCompanion();
        boolean withScientific = columns.get(companionColumn + 2).compareTo("companion_scientific") == 0;
        String type = row.get(companionColumn);

        if (type.compareTo("help")    != 0 && type.compareTo("help eachother")    != 0 &&
            type.compareTo("like")    != 0 && type.compareTo("like eachother")    != 0 &&
            type.compareTo("inhibit") != 0 && type.compareTo("inhibit eachother") != 0 &&
            type.compareTo("dislike") != 0 && type.compareTo("dislike eachother") != 0)
        {
            throw new ParseException(companionColumn, "Invalid companion type " + type);
        }

        parsedCompanion.eachOther =
            type.compareTo("help eachother") == 0 || type.compareTo("like eachother") == 0 ||
            type.compareTo("inhibit eachother") == 0 || type.compareTo("dislike eachother") == 0;
        parsedCompanion.companions =
            parseColumnPlant(row, companionColumn + 1, withScientific);
        parsedCompanion.addToCompanions = type.compareTo("help") == 0 || type.compareTo("inhibit") == 0;
        parsedCompanion.type =
            type.startsWith("help") || type.startsWith("like") ? Companion.Type.GOOD : Companion.Type.BAD;

        int effectColumn = companionColumn + 2 + (withScientific ? 1 : 0);
        String effect = row.get(effectColumn);
        if (effect.compareTo("repel") == 0)
            parsedCompanion.type = parsedCompanion.type == Companion.Type.BAD ? Companion.Type.REPEL_BENEFICIAL : Companion.Type.REPEL_PEST;
        else if (effect.compareTo("attract") == 0)
            parsedCompanion.type = parsedCompanion.type == Companion.Type.BAD ? Companion.Type.ATTRACT_PEST : Companion.Type.ATTRACT_BENEFICIAL;
        else if (effect.compareTo("improve") == 0)
            parsedCompanion.type = Companion.Type.IMPROVE;
        else if (effect.compareTo("trap") == 0)
            parsedCompanion.type = Companion.Type.TRAP_PEST;
        else if (type.compareTo("inhibit") == 0 || type.compareTo("inhibit eachother") == 0)
            parsedCompanion.type = Companion.Type.INHIBIT;
        else if (effect.compareTo("") != 0)
            throw new ParseException(effectColumn, "Invalid companion effect: " + effect);

        int additionalColumn = companionColumn + 3 + (withScientific ? 1 : 0);
        String additional = row.get(additionalColumn);
        if (parsedCompanion.type == Companion.Type.IMPROVE)
        {
            for (String s : additional.split(","))
            {
                try
                {
                    Companion.Improve improve = Companion.Improve.valueOf(s.trim().toUpperCase().replace(' ', '_'));
                    parsedCompanion.improve.add(improve);
                }
                catch (Exception ex)
                {
                    throw new ParseException(additionalColumn, ex.getMessage());
                }
            }
        }
        else if (parsedCompanion.type.withAnimals())
        {
            for (String s : additional.split(","))
            {
                Animal animal = findAnimal(s.trim());
                if (animal == null)
                    throw new ParseException(additionalColumn, s.trim() + " not found");
                parsedCompanion.animals.add(animal);
            }
        }
        return parsedCompanion;
    }

    RootDeepness parseColumnRoot(List<String> row) throws ParseException
    {
        RootDeepness root;
        try
        {
            root = RootDeepness.parseString(row.get(rootColumn));
        }
        catch (Exception ex)
        {
            throw new ParseException(rootColumn, ex.toString());
        }
        return root;
    }

    NutritionalNeeds parseColumnNutritional(List<String> row) throws ParseException
    {
        NutritionalNeeds needs;
        try
        {
            needs = new NutritionalNeeds(NutritionalNeeds.Type.valueOf(row.get(nutritionalColumn).toUpperCase()));
        }
        catch (IllegalArgumentException ex)
        {
            throw new ParseException(nutritionalColumn, ex.toString());
        }
        return needs;
    }

    WeedControl parseColumnWeedControl(List<String> row) throws ParseException
    {
        WeedControl weed;
        try
        {
            weed = new WeedControl(WeedControl.Type.valueOf(row.get(weedColumn).toUpperCase()));
        }
        catch (IllegalArgumentException ex)
        {
            throw new ParseException(weedColumn, ex.toString());
        }
        return weed;
    }

    void parseColumnLifetime()
    {
        /*
                    if (columns[i].compareTo("lifetime") == 0)
                    {
                        Plant.Lifetime newLifetime = Plant.Lifetime.valueOf(values[i].toUpperCase());
                        Plant.Lifetime currentLifetime = foundPlant.getLifetime();
                        if (currentLifetime != null && newLifetime.compareTo(newLifetime) != 0)
                        {
                            errors.append("Conflict lifetime for " + foundPlant.getTranslation("en") +
                                " ( " + foundPlant.getName() + " )" +
                                "; old value=" + currentLifetime.toString() +
                                "; new value=" + newLifetime.toString() + "\r\n");
                            continue;
                        }
                        foundPlant.setLifetime(newLifetime);
                        if (propertiesSource != null)
                            foundPlant.setSource(Plant.SourceType.NUTRITIONAL_NEEDS, propertiesSource);
                    }
                }
            } */
    }

    public void importData(StringBuilder importErrors)
    {
        for (int i = 0; i < values.size(); ++i)
            try
            {
                List<String> row = values.get(i);

                Reference reference = parseColumnReference(row);
                Set<Plant> plants = parseColumnPlant(row, nameColumn, columns.get(nameColumn + 1).compareTo("scientific") == 0);

                if (companionColumn != -1)
                {
                    ParsedCompanion companion = parseColumnCompanion(row);

                    Set<Plant> targets = companion.addToCompanions ? companion.companions : plants;
                    Set<Plant> companions = !companion.addToCompanions ? companion.companions : plants;

                    for (Plant target : targets)
                        for (Plant companionPlant : companions)
                            target.getCompanions().add(companionPlant, companion.type, companion.animals, companion.improve, reference);

                    if (companion.eachOther)
                    {
                        for (Plant target : companions)
                            for (Plant companionPlant : targets)
                                target.getCompanions().add(companionPlant, companion.type, companion.animals, companion.improve, reference);
                    }
                }

                if (rootColumn != -1)
                {
                    RootDeepness newRoot = parseColumnRoot(row);
                    for (Plant plant : plants)
                    {
                        RootDeepness oldRoot = plant.getRootDeepness();
                        if (oldRoot != null && newRoot.compareTo(oldRoot) != 0)
                        {
                            throw new Exception(
                                "Conflict root deepness for " + plant.getTranslation("en") +
                                    " ( " + plant.getName() + " )" +
                                    "; old value=" + oldRoot.toString() +
                                    "; new value=" + newRoot.toString());
                        }
                        plant.setRootDeepness(newRoot);
                        plant.getRootDeepness().references.add(reference);
                    }
                }

                if (nutritionalColumn != -1)
                {
                    NutritionalNeeds newNeeds = parseColumnNutritional(row);
                    for (Plant plant : plants)
                    {
                        NutritionalNeeds oldNeeds = plant.getNutritionalNeeds();
                        if (oldNeeds != null && newNeeds.type.compareTo(oldNeeds.type) != 0)
                        {
                            throw new Exception(
                                "Conflict nutritional needs for " + plant.getTranslation("en") +
                                        " ( " + plant.getName() + " )" +
                                        "; old value=" + oldNeeds.type.toString() +
                                        "; new value=" + newNeeds.type.toString());
                        }
                        plant.setNutritionalNeeds(newNeeds);
                        plant.getNutritionalNeeds().references.add(reference);
                    }
                }

                if (weedColumn != -1)
                {
                    WeedControl newWeed = parseColumnWeedControl(row);
                    for (Plant plant : plants)
                    {
                        WeedControl oldWeed = plant.getWeedControl();
                        if (oldWeed != null && newWeed.type.compareTo(oldWeed.type) != 0)
                        {
                            throw new Exception(
                                    "Conflict weed control for " + plant.getTranslation("en") +
                                            " ( " + plant.getName() + " )" +
                                            "; old value=" + oldWeed.type.toString() +
                                            "; new value=" + newWeed.type.toString());
                        }
                        plant.setWeedControl(newWeed);
                        plant.getWeedControl().references.add(reference);
                    }
                }
            }
            catch (Exception ex)
            {
                importErrors.append(ex.getMessage()).append("\n");
            }

    }

    public void setValue(int rowIndex, int columnIndex, String value)
    {
        values.get(rowIndex).set(columnIndex, value);
        checkForErrors(rowIndex, columnIndex);
    }

    public DataTableModel getDataTableModel() {
        return dataModel;
    }

    public SourceTableModel getSourceTableModel() {
        return sourceModel;
    }

    public TagTableModel getTagTableModel() {
        return tagModel;
    }

    public TaggedPlantsTableModel getTaggedPlantsTableModel() {
        return taggedPlantsModel;
    }

    public void save(File file)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // write sources
            for (int i = 0; i < sources.size(); ++i)
            {
                writer.write("source");
                writer.write("\t");
                writer.write(sources.get(i).name);
                if (!sources.get(i).url.isEmpty())
                {
                    writer.write("\t");
                    writer.write(sources.get(i).url);
                }
                writer.newLine();
            }

            // write tags
            for (Map.Entry<String, ArrayList<String>> tag : tags.entrySet())
            {
                writer.write(tag.getKey());
                for (String plant : tag.getValue())
                {
                    writer.write("\t");
                    writer.write(plant);
                }
                writer.newLine();
            }

            // write columns
            for (int j = 0; j < columns.size(); ++j)
            {
                if (j != 0)
                    writer.write("\t");
                writer.write(columns.get(j));
            }
            writer.newLine();

            // write values
            for (int i = 0; i < values.size(); ++i)
            {
                for (int j = 0; j < columns.size(); ++j)
                {
                    if (j != 0)
                        writer.write("\t");
                    writer.write(values.get(i).get(j));
                }
                writer.newLine();
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
