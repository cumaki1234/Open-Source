package org.sourceforge.kga.translation;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.prefs.Preferences;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.prefs.BackingStoreException;

/**
 * Created by Tiberius on 8/15/2017.
 */
public class TranslationList
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    // Static part
    private Map<String, Translation> translations;
    // private static Translation preferred = null;

    public void load(String translationFolder)
    {
        log.info("Loading available translations");
        translations = new HashMap<>();
        for (String file : Resources.getResourcesInPath(translationFolder))
        {
            log.info("Loading translation file=" + file);

            Properties translationMap = Resources.loadPropertiesFromFile(translationFolder + "/" + file);
            String language = file.substring(0, file.indexOf('.'));
            Translation translation = new Translation(language, translationMap);
            translations.put(language, translation);
        }

        log.info("Loading custom translations");
        try
        {
        	if(Preferences.translation.custom.list.node()!=null) {
        		for (String customLanguage : Preferences.translation.custom.list.node().keys())
        		{
        			Translation t = translations.get(customLanguage);
        			if (t == null)
        			{
        				// custom translations in a not supported language
        				t = new Translation(customLanguage, new Properties());
        				translations.put(customLanguage, t);
        			}
        			Properties custom = new Properties();
        			custom.load(new StringReader(Preferences.translation.custom.list.get(customLanguage)));
        			t.setCustomTranslations(custom);
        		}
        	}
        }
        catch (IOException ex)
        {
            log.warning(ex.toString());
        }
        catch (BackingStoreException ex)
        {
            log.warning(ex.toString());
        }
        /* String language = getLanguageFromPreferences();
        if (!language.isEmpty())
            setCurrentLanguage(language); */
    }

    /*
    public static String getLanguageFromPreferences()
    {
        Preferences prefs = Resources.getPreferences("translation");
        return prefs.get("language", "");
    }
    */

    public Translation get(String language)
    {
        return translations.get(language);
    }

    /**
     * Get available languages.
     * @return A set of string with iso 639 code.
     */
    public Set<String> getLanguages()
    {
        return translations.keySet();
    }

    public Set<Iso639_1.Language> getLanguageItems()
    {
        Set<Iso639_1.Language> ret = new TreeSet<>();
        for (String languageCode : translations.keySet())
            ret.add(Iso639_1.getLanguage(languageCode));
        return ret;
    }
/*
    public static void addTranslation(String language)
    {
        translations.put(language, new Translation(language, "", new Properties()));
    }
*/

	public boolean add(String code, Translation translation) {
		if(!translations.containsKey(code)) {
			translations.put(code, translation);
			return true;
		}
		return false;
		// TODO Auto-generated method stub
		
	}
}
