/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
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
 * Email contact: tiberius.duluman@gmail.com
 */

package org.sourceforge.kga.plant;

import org.sourceforge.kga.Animal;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.translation.Translation;

import java.util.Collection;
import java.util.TreeSet;


public class Companion
{
    public enum Type
    {
        GOOD, IMPROVE, BAD, INHIBIT, ATTRACT_PEST, REPEL_PEST, TRAP_PEST, ATTRACT_BENEFICIAL, REPEL_BENEFICIAL;

        public boolean isBeneficial()
        {
            switch (this)
            {
                case GOOD:
                case IMPROVE:
                case REPEL_PEST:
                case TRAP_PEST:
                case ATTRACT_BENEFICIAL:
                    return true;
                default:
                    return false;
            }
        }

        public boolean withAnimals()
        {
            switch (this)
            {
                case ATTRACT_PEST:
                case REPEL_PEST:
                case ATTRACT_BENEFICIAL:
                case REPEL_BENEFICIAL:
                case TRAP_PEST:
                    return true;
                default:
                    return false;
            }
        }

        public String translate()
        {
            Translation t = Translation.getCurrent();
            switch (this)
            {
                case GOOD:
                    return t.companion_good();
                case IMPROVE:
                    return t.companion_improve();
                case BAD:
                    return t.companion_bad();
                case INHIBIT:
                    return t.companion_inhibit();
                case ATTRACT_PEST:
                    return t.companion_attract_pest();
                case REPEL_PEST:
                    return t.companion_repel_pest();
                case ATTRACT_BENEFICIAL:
                    return t.companion_attract_beneficial();
                case REPEL_BENEFICIAL:
                    return t.companion_repel_beneficial();
                case TRAP_PEST:
                    return t.companion_trap_pest();
            }
            return "";
        }
    }

    public enum Improve
    {
        FLAVOR, GROWTH, HEALTH, PEST_RESISTANCE, SOIL_FERTILITY, VIGOR,ROOT_DEVELOPMENT;

        public String translate()
        {
            Translation t = Translation.getCurrent();
            switch (this)
            {
                case FLAVOR:
                    return t.companion_improve_flavor();
                case GROWTH:
                    return t.companion_improve_growth();
                case HEALTH:
                    return t.companion_improve_health();
                case PEST_RESISTANCE:
                    return t.companion_improve_pest_resistance();
                case SOIL_FERTILITY:
                    return t.companion_improve_soil_fertility();
                case VIGOR:
                    return t.companion_improve_vigor();
                case ROOT_DEVELOPMENT:
                    return t.companion_improve_root_development();
            }
            return "";
        }
    }
    
    public String[] getDetails(){
    	if (type.withAnimals()) {
    		return animals.stream().map(a->Translation.getCurrent().translate(a)).toArray(i->new String[i]);
    	}
    	else if (improve!=null) {
    		return improve.stream().map(a->a.translate()).toArray(i->new String[i]);    		
    	}else {
    		return new String[0];
    	}
    }

    public Plant plant;
    public Type type;
    public TreeSet<Animal> animals;
    public TreeSet<Improve> improve;
    public ReferenceList references = new ReferenceList();
}
