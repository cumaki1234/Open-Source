/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */


package org.sourceforge.kga.translation;

import java.io.*;
import java.util.*;
import java.text.*;

import org.sourceforge.kga.*;
import org.sourceforge.kga.prefs.Preferences;


/**
 * Translation is where we translate to native language.
 * Translation choice is stored in java.util.prefs.Preferences and loads
 * from a xml-file with the name: lowercaseISO639code + ".xml"
 *
 * User can also start program with preferable language as parameter.
 *
 * @author Christian Nilsson, Tiberius Duluman
 *
 */
public class Translation
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    /**
     * Key = lowercase two-letter ISO-639 code. Value = language map.
     */
    private Locale locale;
    private Collator collator;
    private Properties translationsMap;
    private Properties customTranslations;

    static Translation currentTranslation;
    /**
     * Get preferred language set by setCurrentLanguage.
     * @return current language translation
     */
    public static Translation getCurrent()
    {
        return currentTranslation;
    }

    public static String getCurrentFromPreferences()
    {
        return Preferences.translation.language.get();
    }

    public static void setCurrent(Translation translation)
    {
        log.info("Set current translation " + translation.getLanguage());
        currentTranslation = translation;
        try
        {
            Preferences.translation.language.set(currentTranslation.getLanguage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     * @param language lowercase two-letter ISO-639 code.
     */
    public Translation(String language, Properties translationsMap)
    {
        this.translationsMap = translationsMap;
        this.customTranslations = new Properties();
        locale = new Locale(language);
        collator = Collator.getInstance(locale);
    }

    public String getAuthor()
    {
        return translationsMap.getProperty("author");
    }

    public boolean hasMissingTranslations()
    {
        for (Translation.Key key : Key.values())
            if (customTranslations.getProperty(key.toString()) == null &&
                translationsMap.getProperty(key.toString()) == null)
            {
                return true;
            }

        for (Plant plant : Resources.plantList().getPlants())
            if (plant.getTranslation("en") != null &&
                plant.getTranslation(getLanguage()) == null &&
                customTranslations.getProperty(plant.getName()) == null)
            {
                return true;
            }

        return false;
    }

    public void setCustomTranslations(Properties customTranslations)
    {
        for (String s : customTranslations.stringPropertyNames())
            setTranslation(s, customTranslations.getProperty(s));
    }

    public Properties getCustomTranslations()
    {
        return customTranslations;
    }

    public String getDefaultTranslation(Key key)
    {
        return translationsMap.getProperty(key.toString());
    }

    public String getDefaultTranslation(Taxon plant)
    {
        return plant == null ? null : plant.getTranslation(getLanguage());
    }

    public String translate(Key key)
    {
        String k = key.toString();
        String value = customTranslations.getProperty(k);
        if (value == null)
            value = translationsMap.getProperty(k);
        if (value == null)
        {
            System.err.println("Cannot translate " + k);
            return k.replace("_", " ");
        }
        else
        {
            return value;
        }
    }
    
    public String translate(Enum<?> key)
    {
        String k = key.name();
        String value = customTranslations.getProperty(k);
        if (value == null)
            value = translationsMap.getProperty(k);
        if (value == null)
        {
            System.err.println("Cannot translate " + k);
            return k;
        }
        else
        {
            return value;
        }
    }

    public String translate(TaxonVariety plant)
    {
    	return translate(plant.getTaxon());
    }
    
    public String translate(Taxon plant)
        {
        if (plant == null)
            return "null";

        // translation defined by user
        String s = customTranslations.getProperty(plant.getName());
        // translation in current language
        if (s == null)
            s = plant.getTranslation(getLanguage());
        // translation in english
        if (s == null)
            s = plant.getTranslation("en");
        // scientific name
        if (s == null)
            s = plant.getName();
        return s;
    }

    public void setTranslation(String key, String s)
    {
        String t = translationsMap.getProperty(key);
        if (s == null || s.isEmpty() || t != null && t.compareTo(s) == 0)
            customTranslations.remove(key);
        else
            customTranslations.setProperty(key, s);
        saveCustomTranslations();
    }

    public void setTranslation(Taxon taxon, String s)
    {
        customTranslations.remove(taxon.getName());
        String tmp = taxon.getTranslation(getLanguage());
        if (s != null && !s.isEmpty() && (tmp == null || s.compareTo(tmp) != 0))
            customTranslations.setProperty(taxon.getName(), s);
        saveCustomTranslations();
    }

    private void saveCustomTranslations()
    {
        if (customTranslations.size() == 0)
        {
            Preferences.translation.custom.list.remove(getLanguage());
            return;
        }
        StringWriter writer = new StringWriter();
        try
        {
            customTranslations.store(writer, "");
            Preferences.translation.custom.list.set(getLanguage(), writer.toString());
        }
        catch (IOException ex)
        {
            log.warning(ex.toString());
        }
    }

    /**
     * Returns the language code, a lowercase ISO 639 code.
     * @return the language code
     */
    public String getLanguage()
    {
        return locale.getLanguage();
    }

    public Collator getCollator()
    {
        return collator;
    }

    public enum Key
    {
        action_about,
        tag_add,
        action_check_for_update,
        action_contact,
        action_edit_species,
        action_edit_translation,
        action_exit,
        action_export,
        action_export_translation,
        action_garden_statistics,
        action_tags,
        action_help,
        action_language,
        action_new_garden,
        action_new_seed_list,
        action_new_year,
        action_open,
        action_import,
        action_print,
        action_print_setup,
        action_review,
        action_save,
        action_save_as,
        action_seed_manager,
        analyze,
        action_track,
        action_soil_nutrition,
        action_show_an_example,
        tutorial_first_garden,
        action_show_another_example,
        action_species_properties,
        action_upload_to_web,
        action_year_add,
        action_year_delete,
        action_zoom,
        add,
        all,
        allowed_repetitions,
        ask_review,
        automatically_check,
        cancel,
        cannot_add_year,
        choose_year_to_add,
        choose_year_to_delete,
        close,
        comment,
        cost,
        description,
        companion_attract_beneficial,
        companion_attract_pest,
        companion_bad,
        companion_dislike,
        companion_good,
        companion_helped_by,
        companion_improve,
        companion_improve_flavor,
        companion_improve_growth,
        companion_improve_health,
        companion_improve_pest_resistance,
        companion_improve_soil_fertility,
        companion_improve_vigor,
        companion_improve_root_development,
        companion_inhibit,
        companion_repel_beneficial,
        companion_repel_pest,
        companion_trap_pest,
        companion_type,
        debug,
        delete,
        tag_delete,
        tag_delete_confirmation,
        delete_square,
        disadvantageous,
        do_you_want_to_save,
        error,
        error_loading_file,
        error_print,
        error_saving_file,
        expenses,
        harvest,
        expense_allocations,
        family,
        file,
        garden,
        analytics_groupby,
        analytics_metrics,
        plan,
        genus,
        go_to_download,
        info_nutrient_ppm_ranges,
        tag,
        import_csv,
        in,
        invalid_file_format,
        tag_invalid_name,
        invert,
        lifetime_annual,
        lifetime_biennial,
        lifetime_perennial,
        missing_translations,
        measurement_unit_grams,
        measurement_unit_pieces,
        modify,
        tag_modify,
        name,
        new_language,
        new_version_available,
        no_new_version_available,
        none,
        not_tagged,
        nothing_found,
        nutritional_needs,
        nutritional_needs_high,
        nutritional_needs_low,
        nutritional_needs_soil_improver,
        ok,
        only_missing_translations,
        overwrite_file,
        page,
        parent,
        pick_species,
        quantity,
        unit,
        unit_value,
        recent_files,
        remove,
        rename,
        repetition_gap,
        replace,
        replace_all,
        rotation_after,
        rotation_bad,
        rotation_good,
        rotation_repetition_family,
        rotation_repetition_species,
        rotation_root_type_deep,
        rotation_root_type_same_level,
        rotation_root_type_shallow,
        rotation_weed_control_bad,
        rotation_weed_control_good,
        scientific,
        search,
        search_case_insensitive,
        search_direction_by_column,
        search_direction_reverse,
        search_whole_text,
        seed_manager_inventory,
        seed_manager_shopping_list,
        select_plants,
        select_species_first,
        species,
        species_in_garden,
        species_in_inventory,
        total_squares,
        total_years,
        tutorial,
        unknown,
        update_missing_translations,
        variety,
        size,
        pleaseSelectPlant,
        selectPlantToAdd,
        plantDetails,
        weed_control_clear,
        weed_control_weedy,
        usefulLifeYears,
        year,
        years,
        changes_date,
        working_date,
        now,
        move,
        copy,
        seed_manager_tools,
        seed_manager_options,
        seed_manager_autogenerate,
        seed_manager_from_garden,
        seed_manager_from_inventory,
        seed_manager_view_variety,
        seed_manager_view_quantity,
        seed_manager_view_comment,
        seed_manager_view_valid_from,
        seed_manager_view_valid_to,
        valid_from,
        valid_to,
        image,
        grid,
        seed_manager_different_page,
        skip,
        dont_skip,
        sources,
        plant,
        allocation,
        plant_count,
        nitrogen,phosphorus,potassium,ph,calcium,magnesium,zinc,no_content_in_table
    };

    public String action_about()                    { return translate(Key.action_about); }
    public String tag_add()                         { return translate(Key.tag_add); }
    public String action_check_for_update()         { return translate(Key.action_check_for_update); }
    public String action_contact()                  { return translate(Key.action_contact); }
    public String action_edit_species()             { return translate(Key.action_edit_species); }
    public String action_edit_translation()         { return translate(Key.action_edit_translation); }
    public String action_exit()                     { return translate(Key.action_exit); }
    public String harvest()                     	{ return translate(Key.harvest); }
    public String action_export()                   { return translate(Key.action_export); }
    public String action_export_translation()       { return translate(Key.action_export_translation); }
    public String action_garden_statistics()        { return translate(Key.action_garden_statistics); }
    public String action_tags()                     { return translate(Key.action_tags); }
    public String action_help()                     { return translate(Key.action_help); }
    public String action_language()                 { return translate(Key.action_language); }
    public String action_new_garden()               { return translate(Key.action_new_garden); }
    public String action_new_seed_list()            { return translate(Key.action_new_seed_list); }
    public String action_new_year()                 { return translate(Key.action_new_year); }
    public String action_open()                     { return translate(Key.action_open); }
    public String action_import()                   { return translate(Key.action_import); }
    public String action_print()                    { return translate(Key.action_print); }
    public String action_print_setup()              { return translate(Key.action_print_setup); }
    public String action_review()                   { return translate(Key.action_review); }
    public String action_save()                     { return translate(Key.action_save); }
    public String action_save_as()                  { return translate(Key.action_save_as); }
    public String action_seed_manager()             { return translate(Key.action_seed_manager); }
    public String analyze()             { return translate(Key.analyze); }
    public String action_show_an_example()          { return translate(Key.action_show_an_example); }
    public String tutorial_first_garden()          { return translate(Key.tutorial_first_garden); }
    public String action_show_another_example()     { return translate(Key.action_show_another_example); }
    public String action_species_properties()       { return translate(Key.action_species_properties); }
    public String action_upload_to_web()            { return translate(Key.action_upload_to_web); }
    public String action_year_add()                 { return translate(Key.action_year_add); }
    public String action_year_delete()              { return translate(Key.action_year_delete); }
    public String action_zoom()                     { return translate(Key.action_zoom); }
    public String add()                             { return translate(Key.add); }
    public String all()                             { return translate(Key.all); }
    public String allowed_repetitions()             { return translate(Key.allowed_repetitions); }
    public String ask_review()                      { return translate(Key.ask_review); }
    public String automatically_check()             { return translate(Key.automatically_check); }
    public String cancel()                          { return translate(Key.cancel); }
    public String cannot_add_year()                 { return translate(Key.cannot_add_year); }
    public String choose_year_to_add()              { return translate(Key.choose_year_to_add); }
    public String choose_year_to_delete()           { return translate(Key.choose_year_to_delete); }
    public String close()                           { return translate(Key.close); }
    public String comment()                         { return translate(Key.comment); }
    public String companion_attract_beneficial()    { return translate(Key.companion_attract_beneficial); }
    public String companion_attract_pest()          { return translate(Key.companion_attract_pest); }
    public String companion_bad()                   { return translate(Key.companion_bad); }
    public String companion_dislike()               { return translate(Key.companion_dislike); }
    public String companion_good()                  { return translate(Key.companion_good); }
    public String companion_helped_by()             { return translate(Key.companion_helped_by); }
    public String companion_improve()               { return translate(Key.companion_improve); }
    public String companion_improve_flavor()        { return translate(Key.companion_improve_flavor); }
    public String companion_improve_growth()        { return translate(Key.companion_improve_growth); }
    public String companion_improve_health()        { return translate(Key.companion_improve_health); }
    public String companion_improve_pest_resistance() { return translate(Key.companion_improve_pest_resistance); }
    public String companion_improve_soil_fertility(){ return translate(Key.companion_improve_soil_fertility); }
    public String companion_improve_vigor()         { return translate(Key.companion_improve_vigor); }
    public String companion_improve_root_development()         { return translate(Key.companion_improve_root_development); }
    public String companion_inhibit()               { return translate(Key.companion_inhibit); }
    public String companion_repel_beneficial()      { return translate(Key.companion_repel_beneficial); }
    public String companion_repel_pest()            { return translate(Key.companion_repel_pest); }
    public String companion_trap_pest()             { return translate(Key.companion_trap_pest); }
    public String companion_type()             { return translate(Key.companion_type); }
    public String delete()                          { return translate(Key.delete); }
    public String tag_delete()                      { return translate(Key.tag_delete); }
    public String tag_delete_confirmation()         { return translate(Key.tag_delete_confirmation); }
    public String debug()                           { return translate(Key.debug); }
    public String delete_square()                   { return translate(Key.delete_square); }
    public String disadvantageous()                 { return translate(Key.disadvantageous); }
    public String do_you_want_to_save()             { return translate(Key.do_you_want_to_save); }
    public String error()                           { return translate(Key.error); }
    public String error_loading_file()              { return translate(Key.error_loading_file); }
    public String error_print()                     { return translate(Key.error_print); }
    public String error_saving_file()               { return translate(Key.error_saving_file); }
    public String family()                          { return translate(Key.family); }
    public String file()                            { return translate(Key.file); }
    public String garden()                          { return translate(Key.garden); }
    public String genus()                           { return translate(Key.genus); }
    public String go_to_download()                  { return translate(Key.go_to_download); }
    public String tag()                             { return translate(Key.tag); }
    public String import_csv()                      { return translate(Key.import_csv); }
    public String in()                              { return translate(Key.in); }
    public String invalid_file_format()             { return translate(Key.invalid_file_format); }
    public String tag_invalid_name()                { return translate(Key.tag_invalid_name); }
    public String invert()                          { return translate(Key.invert); }
    public String lifetime_annual()                 { return translate(Key.lifetime_annual); }
    public String lifetime_biennial()               { return translate(Key.lifetime_biennial); }
    public String lifetime_perennial()              { return translate(Key.lifetime_perennial); }
    public String measurement_unit_grams()          { return translate(Key.measurement_unit_grams); }
    public String measurement_unit_pieces()         { return translate(Key.measurement_unit_pieces); }
    public String missing_translations()            { return translate(Key.missing_translations); }
    public String modify()                          { return translate(Key.modify); }
    public String tag_modify()                      { return translate(Key.tag_modify); }
    public String name()                            { return translate(Key.name); }
    public String new_language()                    { return translate(Key.new_language); }
    public String new_version_available()           { return translate(Key.new_version_available); }
    public String no_new_version_available()        { return translate(Key.no_new_version_available); }
    public String none()                            { return translate(Key.none); }
    public String not_tagged()                      { return translate(Key.not_tagged); }
    public String nothing_found()                   { return translate(Key.nothing_found); }
    public String nutritional_needs()               { return translate(Key.nutritional_needs); }
    public String nutritional_needs_high()          { return translate(Key.nutritional_needs_high); }
    public String nutritional_needs_low()           { return translate(Key.nutritional_needs_low); }
    public String nutritional_needs_soil_improver() { return translate(Key.nutritional_needs_soil_improver); }
    public String info_nutrient_ppm_ranges() { return translate(Key.info_nutrient_ppm_ranges); }
    public String ok()                              { return translate(Key.ok); }
    public String only_missing_translations()       { return translate(Key.only_missing_translations); }
    public String overwrite_file()                  { return translate(Key.overwrite_file); }
    public String page()                            { return translate(Key.page); }
    public String parent()                          { return translate(Key.parent); }
    public String quantity()                        { return translate(Key.quantity); }
    public String unit()                        	{ return translate(Key.unit); }
    public String pick_species()                    { return translate(Key.pick_species); }
    public String plan()                   			{ return translate(Key.plan); }
    public String recent_files()                    { return translate(Key.recent_files); }
    public String remove()                          { return translate(Key.remove); }
    public String rename()                          { return translate(Key.rename); }
    public String repetition_gap()                  { return translate(Key.repetition_gap); }
    public String replace()                         { return translate(Key.replace); }
    public String replace_all()                     { return translate(Key.replace_all); }
    public String rotation_after()                  { return translate(Key.rotation_after); }
    public String rotation_bad()                    { return translate(Key.rotation_bad); }
    public String rotation_good()                   { return translate(Key.rotation_good); }
    public String rotation_repetition_family()      { return translate(Key.rotation_repetition_family); }
    public String rotation_repetition_species()     { return translate(Key.rotation_repetition_species); }
    public String rotation_root_type_deep()         { return translate(Key.rotation_root_type_deep); }
    public String rotation_root_type_same_level()   { return translate(Key.rotation_root_type_same_level); }
    public String rotation_root_type_shallow()      { return translate(Key.rotation_root_type_shallow); }
    public String rotation_weed_control_bad()       { return translate(Key.rotation_weed_control_bad); }
    public String rotation_weed_control_good()      { return translate(Key.rotation_weed_control_good); }
    public String scientific()                      { return translate(Key.scientific); }
    public String search()                          { return translate(Key.search); }
    public String search_case_insensitive()         { return translate(Key.search_case_insensitive); }
    public String search_direction_by_column()      { return translate(Key.search_direction_by_column); }
    public String search_direction_reverse()        { return translate(Key.search_direction_reverse); }
    public String search_whole_text()               { return translate(Key.search_whole_text); }
    public String seed_manager_inventory()          { return translate(Key.seed_manager_inventory); }
    public String seed_manager_shopping_list()      { return translate(Key.seed_manager_shopping_list); }
    public String select_plants()                   { return translate(Key.select_plants); }
    public String select_species_first()            { return translate(Key.select_species_first); }
    public String species()                         { return translate(Key.species); }
    public String species_in_garden()               { return translate(Key.species_in_garden); }
    public String species_in_inventory()            { return translate(Key.species_in_inventory); }
    public String total_squares()                   { return translate(Key.total_squares); }
    public String total_years()                     { return translate(Key.total_years); }
    public String tutorial()                        { return translate(Key.tutorial); }
    public String unknown()                         { return translate(Key.unknown); }
    public String update_missing_translations()     { return translate(Key.update_missing_translations); }
    public String variety()                         { return translate(Key.variety); }
    public String size()                         { return translate(Key.size); }
    public String nitrogen()                         { return translate(Key.nitrogen); }
    public String phosphorus()                         { return translate(Key.phosphorus); }
    public String potassium()                         { return translate(Key.potassium); }
    public String ph()                         { return translate(Key.ph); }
    public String calcium()                         { return translate(Key.calcium); }
    public String magnesium()                         { return translate(Key.magnesium); }
    public String zinc()                         { return translate(Key.zinc); }
    public String plantDetails()                         { return translate(Key.plantDetails); }
    public String pleaseSelectPlant()                         { return translate(Key.pleaseSelectPlant); }
    public String selectPlantToAdd()                         { return translate(Key.selectPlantToAdd); }
    public String weed_control_clear()              { return translate(Key.weed_control_clear); }
    public String weed_control_weedy()              { return translate(Key.weed_control_weedy); }
    public String year()                            { return translate(Key.year); }
    public String years()                           { return translate(Key.years); }
    public String changes_date()                    { return translate(Key.changes_date); }
    public String working_date()                    { return translate(Key.working_date); }
    public String now()                             { return translate(Key.now); }
    public String move()                            { return translate(Key.move); }
    public String copy()                            { return translate(Key.copy); }
    public String seed_manager_tools()              { return translate(Key.seed_manager_tools); }
    public String seed_manager_options()            { return translate(Key.seed_manager_options); }
    public String seed_manager_autogenerate()       { return translate(Key.seed_manager_autogenerate); }
    public String seed_manager_from_garden()        { return translate(Key.seed_manager_from_garden); }
    public String seed_manager_from_inventory()     { return translate(Key.seed_manager_from_inventory); }
    public String seed_manager_view_variety()       { return translate(Key.seed_manager_view_variety); }
    public String seed_manager_view_quantity()      { return translate(Key.seed_manager_view_quantity); }
    public String seed_manager_view_comment()       { return translate(Key.seed_manager_view_comment); }
    public String seed_manager_view_valid_from()    { return translate(Key.seed_manager_view_valid_from); }
    public String seed_manager_view_valid_to()      { return translate(Key.seed_manager_view_valid_to); }
    public String valid_from()                      { return translate(Key.valid_from); }
    public String valid_to()                        { return translate(Key.valid_to); }
    public String image()                           { return translate(Key.image); }
    public String grid()                            { return translate(Key.grid); }
    public String seed_manager_different_page()     { return translate(Key.seed_manager_different_page); }
    public String skip()                            { return translate(Key.skip); }
    public String dont_skip()                       { return translate(Key.dont_skip); }
    public String analytics_groupby()                       { return translate(Key.analytics_groupby); }
    public String analytics_metrics()                       { return translate(Key.analytics_metrics); }
    public String Sources()                       { return translate(Key.sources); }
    public String getTablePlaceholder()                       { return translate(Key.no_content_in_table); }
}
