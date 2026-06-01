import { createContext, useContext, useState } from "react";
import { GraffitiMonths }  from "../resources/assets/images/Graffiti_Months/index.js";
import { GraffitiNumbers } from "../resources/assets/images/Graffiti_Numbers/index.js";
import { GraffitiDays }    from "../resources/assets/images/Graffiti_Days/index.js";
import { GraffitiLetters } from "../resources/assets/images/Graffiti_Letters/index.js";
import { GraffitiSettings } from "../resources/assets/images/Graffiti_Settings/index.js";
import GraffitiYears from "../resources/assets/images/Graffiti_Years/Years.gif"
import { MingLiUMonths }   from "../resources/assets/images/MingLiU_Months/index.js";
import { MingLiUNumbers }  from "../resources/assets/images/MingLiU_Numbers/index.js";
import { MingLiUDays }     from "../resources/assets/images/MingLiU_Days/index.js";
import { MingLiULetters }  from "../resources/assets/images/MingLiU_Letters/index.js";
import { MingLiUSettings } from "../resources/assets/images/MingLiU_Settings/index.js"
import MingLiUYears  from "../resources/assets/images/MingLiU_Years/Years.gif"

export const FONT_THEMES = {
  graffiti: {
    label:   "Graffiti",
    Months:  GraffitiMonths,
    Numbers: GraffitiNumbers,
    Days:    GraffitiDays,
    Letters: GraffitiLetters,
    Settings: GraffitiSettings,

    /* Temp Impl*/
    Years: GraffitiYears,
  },
  mingliu: {
    label:   "MingLiU",
    Months:  MingLiUMonths,
    Numbers: MingLiUNumbers,
    Days:    MingLiUDays,
    Letters: MingLiULetters,
    Settings: MingLiUSettings,
    
    /* Temp Impl*/
    Years: MingLiUYears,
  },
};

const FontPackage = createContext(null);

export function FontProvider({ children }) {
  const [fontKey, setFontKey] = useState(
    () => localStorage.getItem("calisigh-font") ?? "graffiti"
  );

  function setFontTheme(key) {
    setFontKey(key);
    localStorage.setItem("calisigh-font", key);
  }

  return (
    <FontPackage.Provider value={{ fontKey, fontTheme: FONT_THEMES[fontKey], setFontTheme }}>
      {children}
    </FontPackage.Provider>
  );
}

export function useFontTheme() {
  return useContext(FontPackage);
}