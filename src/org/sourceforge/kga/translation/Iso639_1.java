package org.sourceforge.kga.translation;

import java.text.Normalizer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Author: tiberius.duluman
 * Date: 12/31/13
 */
public class Iso639_1
{
    public static class Language implements Comparable<Language>
    {
        public Language(String code, String name)
        {
            this.code = code;
            this.name = name;
        }

        public String code;
        public String name;
        public String toString() { return name; }

        @Override
        public int compareTo(Language o)
        {
            if (code.equals("en"))
                return -1;
            if (o.code.equals("en"))
                return 1;
            return code.compareTo(o.code);
        }
    }

    static final private Language[] languages = new Language[] {
        new Language("en", "English"),
        new Language("aa", "Afaraf"),
        new Language("ab", "Aҧсшәа"),
        new Language("ae", "Avesta"),
        new Language("af", "Afrikaans"),
        new Language("ak", "Akan"),
        new Language("an", "Aragonés"),
        new Language("ar", "العربية"),
        new Language("as", "অসমীয়া"),
        new Language("av", "Aвар мацӀ"),
        new Language("ay", "Aymar aru"),
        new Language("az", "Azərbaycan dili"),
        new Language("ba", "башҡорт теле"),
        new Language("be", "беларуская мова"),
        new Language("bg", "български език"),
        new Language("bh", "भोजपुरी"),
        new Language("bi", "Bislama"),
        new Language("bm", "Bamanankan"),
        new Language("bn", "বাংলা"),
        new Language("bo", "བོད་ཡིག"),
        new Language("br", "Brezhoneg"),
        new Language("bs", "Bosanski jezik"),
        new Language("ca", "Català"),
        new Language("ce", "нохчийн мотт"),
        new Language("ch", "Chamoru"),
        new Language("co", "Corsu"),
        new Language("cr", "ᓀᐦᐃᔭᐍᐏᐣ"),
        new Language("cs", "čeština"),
        new Language("cu", "ѩзыкъ словѣньскъ"),
        new Language("cv", "чӑваш чӗлхи"),
        new Language("cy", "Cymraeg"),
        new Language("da", "Dansk"),
        new Language("de", "Deutsch"),
        new Language("dz", "རྫོང་ཁ"),
        new Language("ee", "Eʋegbe"),
        new Language("el", "ελληνικά"),
        new Language("eo", "Esperanto"),
        new Language("es", "Español"),
        new Language("et", "Eesti"),
        new Language("eu", "Euskara"),
        new Language("fa", "فارسی"),
        new Language("ff", "Fulfulde"),
        new Language("fi", "Suomi"),
        new Language("fj", "Vosa Vakaviti"),
        new Language("fo", "Føroyskt"),
        new Language("fr", "Français"),
        new Language("fy", "Frysk"),
        new Language("ga", "Gaeilge"),
        new Language("gd", "Gàidhlig"),
        new Language("gl", "Galego"),
        new Language("gn", "Avañe'ẽ"),
        new Language("gu", "ગુજરાતી"),
        new Language("gv", "Gaelg"),
        new Language("ha", "(Hausa) هَوُسَ"),
        new Language("he", "עברית"),
        new Language("hi", "हिन्दी, हिंदी"),
        new Language("ho", "Hiri Motu"),
        new Language("hr", "Hrvatski jezik"),
        new Language("ht", "Kreyòl ayisyen"),
        new Language("hu", "Magyar"),
        new Language("hy", "Հայերեն"),
        new Language("hz", "Otjiherero"),
        new Language("ia", "Interlingua"),
        new Language("id", "Bahasa Indonesia"),
        new Language("ie", "Interlingue"),
        new Language("ig", "Asụsụ Igbo"),
        new Language("ik", "Iñupiaq, Iñupiatun"),
        new Language("io", "Ido"),
        new Language("is", "Íslenska"),
        new Language("it", "Italiano"),
        new Language("ja", "日本語 (にほんご)"),
        new Language("jv", "ꦧꦱꦗꦮ, Basa Jawa"),
        new Language("ka", "ქართული"),
        new Language("kg", "Kikongo"),
        new Language("ki", "Gĩkũyũ"),
        new Language("kj", "Kuanyama"),
        new Language("kk", "қазақ тілі"),
        new Language("kl", "Kalaallisut"),
        new Language("km", "ខ្មែរ, ខេមរភាសា, ភាសាខ្មែរ"),
        new Language("kn", "ಕನ್ನಡ"),
        new Language("ko", "한국어"),
        new Language("kr", "Kanuri"),
        new Language("ks", "कश्मीरी, كشميري"),
        new Language("ku", "Kurdî, کوردی"),
        new Language("kv", "коми кыв"),
        new Language("kw", "Kernewek"),
        new Language("ky", "Кыргызча, Кыргыз тили"),
        new Language("la", "Latine"),
        new Language("lb", "Lëtzebuergesch"),
        new Language("lg", "Luganda"),
        new Language("li", "Limburgs"),
        new Language("ln", "Lingála"),
        new Language("lo", "ພາສາລາວ"),
        new Language("lt", "Lietuvių kalba"),
        new Language("lv", "Latviešu valoda"),
        new Language("mg", "Malagasy fiteny"),
        new Language("mh", "Kajin M̧ajeļ"),
        new Language("mi", "Te reo Māori"),
        new Language("mk", "македонски јазик"),
        new Language("ml", "മലയാളം"),
        new Language("mn", "Монгол хэл"),
        new Language("mr", "मराठी"),
        new Language("ms", "Bahasa Melayu, بهاس ملايو"),
        new Language("mt", "Malti"),
        new Language("my","ဗမာစာ"),
        new Language("na", "Dorerin Naoero"),
        new Language("nb", "Norsk Bokmål"),
        new Language("nd", "IsiNdebele"),
        new Language("ne", "नेपाली"),
        new Language("ng", "Owambo"),
        new Language("nl", "Nederlands"),
        new Language("nn", "Norsk nynorsk"),
        new Language("no", "Norsk"),
        new Language("nr", "IsiNdebele"),
        new Language("nv", "Diné bizaad"),
        new Language("ny", "ChiCheŵa"),
        new Language("oc", "Occitan"),
        new Language("om", "Afaan Oromoo"),
        new Language("or", "ଓଡ଼ିଆ"),
        new Language("os", "ирон æвзаг"),
        new Language("pa", "ਪੰਜਾਬੀ, پنجابی"),
        new Language("pi", "पालि, पाळि"),
        new Language("pl", "język polski"),
        new Language("ps", "پښتو"),
        new Language("pt", "Português"),
        new Language("qu", "Runa Simi"),
        new Language("rm", "Rumantsch grischun"),
        new Language("rn", "Ikirundi"),
        new Language("ro", "Română"),
        new Language("ru", "русский"),
        new Language("rw", "Ikinyarwanda"),
        new Language("sa", "संस्कृतम्"),
        new Language("sc", "Sardu"),
        new Language("sd", "à¤¸à¤¿à¤¨à¥�à¤§à¥€"),
        new Language("se", "सिन्धी, سنڌي، سندھی"),
        new Language("sg", "yângâ tî sängö"),
        new Language("sk", "Slovenčina"),
        new Language("sl", "Slovenščina"),
        new Language("sm", "Gagana fa'a Samoa"),
        new Language("sn", "ChiShona"),
        new Language("so", "Soomaaliga"),
        new Language("sq", "Shqip"),
        new Language("sr", "српски језик"),
        new Language("ss", "SiSwati"),
        new Language("st", "Sesotho"),
        new Language("su", "Basa Sunda"),
        new Language("sv", "Svenska"),
        new Language("sw", "Kiswahili"),
        new Language("ta", "தமிழ்"),
        new Language("te", "తెలుగు"),
        new Language("tg", "тоҷикӣ, toçikī, تاجیکی"),
        new Language("th", "ไทย"),
        new Language("tk", "Türkmen"),
        new Language("tl", "Wikang Tagalog"),
        new Language("tn", "Setswana"),
        new Language("to", "Faka Tonga"),
        new Language("tr", "Türkçe"),
        new Language("ts", "Xitsonga"),
        new Language("tt", "татар теле"),
        new Language("tw", "Twi"),
        new Language("ty", "Reo Tahiti"),
        new Language("ug", "ئۇيغۇرچە"),
        new Language("uk", "Українська"),
        new Language("ur", "اردو"),
        new Language("uz", "Oʻzbek"),
        new Language("ve", "Tshivenḓa"),
        new Language("vi", "Tiếng Việt"),
        new Language("vo", "Volapük"),
        new Language("wa", "Walon"),
        new Language("wo", "Wollof"),
        new Language("xh", "IsiXhosa"),
        new Language("yi", "ייִדיש"),
        new Language("yo", "Yorùbá"),
        new Language("za", "Saɯ cueŋƅ"),
        new Language("zh", "中文 (Zhōngwén)"),
        new Language("zu", "IsiZulu") };

    static public String getLanguageName(String code)
    {
        Language language = getLanguage(code);
        if (language == null)
            return null;
        return language.name;
    }

    static public Language getLanguage(String code)
    {
        if (code.equals("en"))
            return languages[0];
        int i = 1, j = languages.length;
        while (true)
        {
            int k = i + (j - i) / 2;
            Language middle = languages[k];
            int pos = code.compareTo(middle.code);
            if (pos == 0)
                return middle;
            else if (k == i)
                return null;
            else if (pos < 0)
                j = k;
            else
                i = k + 1;
        }
    }

    static Language[] languagesSet = null;
    static public Language[] getLanguages()
    {
        if (languagesSet == null)
        {
            Set<Language> set = new TreeSet<>();
            for (Language language : languages)
                set.add(language);
            languagesSet = new Language[set.size()];
            set.toArray(languagesSet);
        }
        return languagesSet;
    }
    
    static public Language getbyName(String name) {
    	for (Language l : languages) {
    		if(l.name.toUpperCase().equals(name.toUpperCase()))
    			return l;
        }
    	return null;
    }

    static public String simplifyString(String text)
    {
        return Normalizer.normalize(text, Normalizer.Form.NFD).toLowerCase();
    }

    static public boolean containsText(String into, String find)
    {
        return simplifyString(into).contains(simplifyString(find));
    }
}
