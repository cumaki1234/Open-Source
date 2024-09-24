rem updated for inkscape 1.0.1
SET SIZE=9

rem del *.png

SET INKSCAPE="C:\Program Files (x86)\Inkscape\bin\Inkscape.exe"
SET SED="C:\Program Files (x86)\GnuWin32\bin\sed.exe"

%INKSCAPE% -w 24 -h 24 -o folder.png folder.svg
%INKSCAPE% -w 24 -h 24 -o filter.png filter.svg
%INKSCAPE% -w 24 -h 24 -o file.png file.svg
%INKSCAPE% -w 24 -h 24 -o cursor.png cursor.svg
%INKSCAPE% --export-area=0:0:12:12 -w %SIZE% -h %SIZE% -o good.png good.svg
%INKSCAPE% --export-area=0:0:12:12 -w 24 -h 24 -o add_cursor.png good.svg
%INKSCAPE% --export-area=0:0:12:12 -w %SIZE% -h %SIZE% -o bad.png bad.svg
%INKSCAPE% --export-area=0:0:12:12 -w %SIZE% -h %SIZE% -o equal.png equal.svg
%INKSCAPE% -w 24 -h 24 -o picker.png picker.svg
%INKSCAPE% -w 32 -h 32 -o picker_cursor.png picker_cursor.svg
%INKSCAPE% -w 24 -h 24 -o delete.png delete.svg
%INKSCAPE% -w 32 -h 32 -o delete_cursor.png delete_cursor.svg

%SED% "s/ANGLE/0/g"   good_arrow.svg > good_arrow_w.svg
%SED% "s/ANGLE/45/g"  good_arrow.svg > good_arrow_nw.svg
%SED% "s/ANGLE/90/g"  good_arrow.svg > good_arrow_n.svg
%SED% "s/ANGLE/135/g" good_arrow.svg > good_arrow_ne.svg
%SED% "s/ANGLE/180/g" good_arrow.svg > good_arrow_e.svg
%SED% "s/ANGLE/225/g" good_arrow.svg > good_arrow_se.svg
%SED% "s/ANGLE/270/g" good_arrow.svg > good_arrow_s.svg
%SED% "s/ANGLE/315/g" good_arrow.svg > good_arrow_sw.svg

%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_w.png good_arrow_w.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_nw.png good_arrow_nw.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_n.png good_arrow_n.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_ne.png good_arrow_ne.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_e.png good_arrow_e.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_se.png good_arrow_se.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_s.png good_arrow_s.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o good_arrow_sw.png good_arrow_sw.svg

%SED% "s/ANGLE/0/g"   bad_arrow.svg > bad_arrow_w.svg
%SED% "s/ANGLE/45/g"  bad_arrow.svg > bad_arrow_nw.svg
%SED% "s/ANGLE/90/g"  bad_arrow.svg > bad_arrow_n.svg
%SED% "s/ANGLE/135/g" bad_arrow.svg > bad_arrow_ne.svg
%SED% "s/ANGLE/180/g" bad_arrow.svg > bad_arrow_e.svg
%SED% "s/ANGLE/225/g" bad_arrow.svg > bad_arrow_se.svg
%SED% "s/ANGLE/270/g" bad_arrow.svg > bad_arrow_s.svg
%SED% "s/ANGLE/315/g" bad_arrow.svg > bad_arrow_sw.svg

%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_w.png bad_arrow_w.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_nw.png bad_arrow_nw.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_n.png bad_arrow_n.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_ne.png bad_arrow_ne.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_e.png bad_arrow_e.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_se.png bad_arrow_se.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_s.png bad_arrow_s.svg
%INKSCAPE% -w %SIZE% -h %SIZE% -o bad_arrow_sw.png bad_arrow_sw.svg

del good_arrow_*.svg
del bad_arrow_*.svg
