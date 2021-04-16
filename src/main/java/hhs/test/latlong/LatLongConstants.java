package hhs.test.latlong;

import java.util.*;

/**
 * Define a bunch of LAT/LONG values into the "hhs.rep_location_search" table.  There are four sets of data
 * to test against.  All are relative to Bloomington, IN: repId=4049054, Lat/Long=39.1653,-86.5264
 * <ul>
 *   <li>3.7KM -- reps with 3.7 KM of Bloomington IN</li>
 *   <li>7.3 KM -- reps within 7.3 KM of Bloomington, IN</li>
 *   <li>12.7 KM -- reps within 12.7 KM of Bloomington, IN</li>
 *   <li>17.2 KM -- reps within 17.2 KM of Bloomington, IN</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class LatLongConstants {

    static final Set<Integer> DIST_3_7 = new TreeSet<>();
    static final Set<Integer> DIST_7_3 = new TreeSet<>();
    static final Set<Integer> DIST_12_7 = new TreeSet<>();
    static final Set<Integer> DIST_17_2 = new TreeSet<>();

    static final List<String> INSERT_REPS = new ArrayList<>();

    static {
        loadRepsIdsForDistances();
        loadInsertStatements();
    }

    static void loadRepsIdsForDistances() {
        // 16 Entries
        DIST_3_7.addAll(Arrays.asList(
            392686, 4049054, 10718196, 4110781, 8260537, 10718134, 4110653, 8260411, 4112138, 4116616,
            8266304, 4035429, 4049640, 4050459, 4047775, 8229916));

        // 43 Entries
        DIST_7_3.addAll(Arrays.asList(
            392686, 4049054, 10718196, 4110781, 8260537, 10718134, 4110653, 8260411, 4112138, 4116616, 
            8266304, 4035429, 4049640, 4050459, 4047775, 8229916, 4117450, 4043383, 4107583, 4106896, 
            4042752, 4110677, 4045160, 8260435, 4045642, 4114633, 8264352, 4048657, 8228128, 4099995, 
            4118061, 8253544, 4046931, 4048687, 8228158, 4117759, 8267432, 4101666, 4100015, 8253564, 
            10718098, 4051290, 8230739));

        // 110 Entries
        DIST_12_7.addAll(Arrays.asList(
            392686, 4049054, 10718196, 4110781, 8260537, 10718134, 4110653, 8260411, 4112138, 4116616, 
            8266304, 4035429, 4049640, 4050459, 4047775, 8229916, 4117450, 4043383, 4107583, 4106896, 
            4042752, 4110677, 4045160, 8260435, 4045642, 4114633, 8264352, 4048657, 8228128, 4099995, 
            4118061, 8253544, 4046931, 4048687, 8228158, 4117759, 8267432, 4101666, 4100015, 8253564, 
            10718098, 4051290, 8230739, 4112401, 4046856, 4099520, 4112657, 4116155, 8262396, 8012520, 
            8012521, 4052134, 4046069, 8225571, 4046878, 4110953, 4114392, 8264112, 4109987, 8259757, 
            4106991, 4118927, 4044367, 8268593, 8223903, 8012522, 4099334, 4106390, 4117736, 8267409, 
            4109015, 4051848, 8231293, 10731851, 4050448, 4106597, 4109158, 4049378, 8228840, 4110157, 
            4051710, 4044378, 4044724, 4045000, 8224257, 8224525, 8259921, 8223914, 8231156, 10718142, 
            4114669, 4045237, 8224755, 8264388, 4109295, 4114094, 8259074, 4117737, 8267410, 4118078, 
            4099309, 8252873, 4045236, 8224754, 4115078, 4107588, 4118041, 10718152, 4049370, 8228832));

        // 184 Entries
        DIST_17_2.addAll(Arrays.asList(
            392686, 4049054, 10718196, 4110781, 8260537, 10718134, 4110653, 8260411, 4112138, 4116616, 
            8266304, 4035429, 4049640, 4050459, 4047775, 8229916, 4117450, 4043383, 4107583, 4106896, 
            4042752, 4110677, 4045160, 8260435, 4045642, 4114633, 8264352, 4048657, 8228128, 4099995, 
            4118061, 8253544, 4046931, 4048687, 8228158, 4117759, 8267432, 4101666, 4100015, 8253564, 
            10718098, 4051290, 8230739, 4112401, 4046856, 4099520, 4112657, 4116155, 8262396, 8012520, 
            8012521, 4052134, 4046069, 8225571, 4046878, 4110953, 4114392, 8264112, 4109987, 8259757, 
            4106991, 4118927, 4044367, 8268593, 8223903, 8012522, 4099334, 4106390, 4117736, 8267409, 
            4109015, 4051848, 8231293, 10731851, 4050448, 4106597, 4109158, 4049378, 8228840, 4110157, 
            4051710, 4044378, 4044724, 4045000, 8224257, 8224525, 8259921, 8223914, 8231156, 10718142, 
            4114669, 4045237, 8224755, 8264388, 4109295, 4114094, 8259074, 4117737, 8267410, 4118078, 
            4099309, 8252873, 4045236, 8224754, 4115078, 4107588, 4118041, 4119648, 8269309, 10718152, 
            4049370, 8228832, 4119456, 8269117, 4115366, 8012587, 8265071, 4101696, 4114599, 4048241, 
            8227712, 8264318, 4050313, 4106111, 4113325, 8263060, 4107747, 4119349, 4051089, 4044961, 
            8224486, 8230538, 4046554, 4043280, 4048156, 8226054, 8227627, 8222832, 4099996, 8253545, 
            4046859, 8012470, 8028078, 4049035, 4047692, 8227171, 4042787, 4045208, 8224727, 4048256, 
            8227727, 5797367, 4049874, 8229334, 8680755, 4117175, 4043266, 4045251, 8266851, 8222819, 
            8224769, 4045020, 8224545, 4100496, 4111543, 4050982, 4042734, 8254041, 4048660, 8228131, 
            4107598, 4048235, 8227706, 4042825, 4114276, 4114654, 4116548, 4045307, 4049864, 8266236, 
            8229324, 8224824, 8263997, 8264373));
    }

    static void loadInsertStatements() {
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('392686', 'POINT(39.16528 -86.52639)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049054', 'POINT(39.16528 -86.52639)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10718196', 'POINT(39.167 -86.5222)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4110781', 'POINT(39.16639 -86.54806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8260537', 'POINT(39.16639 -86.54806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10718134', 'POINT(39.1505 -86.5083)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4110653', 'POINT(39.16722 -86.49806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8260411', 'POINT(39.16722 -86.49806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4112138', 'POINT(39.1519 -86.5031)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4116616', 'POINT(39.16972 -86.55806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8266304', 'POINT(39.16972 -86.55806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4035429', 'POINT(39.1839 -86.55)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049640', 'POINT(39.1397 -86.5383)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4050459', 'POINT(39.14139 -86.50917)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4047775', 'POINT(39.1906 -86.5389)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8229916', 'POINT(39.14139 -86.50917)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4117450', 'POINT(39.1319 -86.5311)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4043383', 'POINT(39.1736 -86.5736)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4107583', 'POINT(39.20694 -86.51944)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4106896', 'POINT(39.1225 -86.51889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4042752', 'POINT(39.1608 -86.5836)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4110677', 'POINT(39.16417 -86.58667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045160', 'POINT(39.1406 -86.5772)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8260435', 'POINT(39.16417 -86.58667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045642', 'POINT(39.2131 -86.5233)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114633', 'POINT(39.18639 -86.46778)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8264352', 'POINT(39.18639 -86.46778)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048657', 'POINT(39.21306 -86.505)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8228128', 'POINT(39.21306 -86.505)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4099995', 'POINT(39.1225 -86.48972)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4118061', 'POINT(39.1358 -86.5808)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8253544', 'POINT(39.1225 -86.48972)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046931', 'POINT(39.2103 -86.5608)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048687', 'POINT(39.21222 -86.49556)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8228158', 'POINT(39.21222 -86.49556)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4117759', 'POINT(39.19944 -86.58333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8267432', 'POINT(39.19944 -86.58333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4101666', 'POINT(39.1092 -86.54)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4100015', 'POINT(39.10917 -86.49889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8253564', 'POINT(39.10917 -86.49889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10718098', 'POINT(39.1064 -86.5382)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4051290', 'POINT(39.16528 -86.61139)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8230739', 'POINT(39.16528 -86.61139)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4112401', 'POINT(39.2111 -86.4622)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046856', 'POINT(39.1367 -86.61)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4099520', 'POINT(39.0958 -86.4939)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4112657', 'POINT(39.23889 -86.50056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4116155', 'POINT(39.2144 -86.6011)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8262396', 'POINT(39.23889 -86.50056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8012520', 'POINT(39.1167 -86.45)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8012521', 'POINT(39.1167 -86.45)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4052134', 'POINT(39.2403 -86.4992)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046069', 'POINT(39.23722 -86.57306)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8225571', 'POINT(39.23722 -86.57306)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046878', 'POINT(39.1253 -86.4331)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4110953', 'POINT(39.0797 -86.5133)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114392', 'POINT(39.07861 -86.54028)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8264112', 'POINT(39.07861 -86.54028)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4109987', 'POINT(39.1175 -86.42944)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8259757', 'POINT(39.1175 -86.42944)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4106991', 'POINT(39.21472 -86.62722)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4118927', 'POINT(39.25722 -86.51528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4044367', 'POINT(39.14944 -86.64333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8268593', 'POINT(39.25722 -86.51528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8223903', 'POINT(39.14944 -86.64333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8012522', 'POINT(39.0833 -86.5833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4099334', 'POINT(39.07444 -86.498329)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4106390', 'POINT(39.12306 -86.63278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4117736', 'POINT(39.08583 -86.59139)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8267409', 'POINT(39.08583 -86.59139)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4109015', 'POINT(39.07111 -86.5069)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4051848', 'POINT(39.24972 -86.46611)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8231293', 'POINT(39.24972 -86.46611)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10731851', 'POINT(39.07084 -86.5026)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4050448', 'POINT(39.229721 -86.621391)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4106597', 'POINT(39.12361 -86.41028)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4109158', 'POINT(39.2661 -86.5208)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049378', 'POINT(39.17556 -86.39667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8228840', 'POINT(39.17556 -86.39667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4110157', 'POINT(39.1675 -86.65972)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4051710', 'POINT(39.06306 -86.54167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4044378', 'POINT(39.12972 -86.40167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4044724', 'POINT(39.10889 -86.63806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045000', 'POINT(39.105829 -86.634439)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224257', 'POINT(39.10889 -86.63806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224525', 'POINT(39.10583 -86.63444)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8259921', 'POINT(39.1675 -86.65972)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8223914', 'POINT(39.12972 -86.40167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8231156', 'POINT(39.06306 -86.54167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10718142', 'POINT(39.1669 -86.6606)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114669', 'POINT(39.25917 -86.58611)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045237', 'POINT(39.22944 -86.42)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224755', 'POINT(39.22944 -86.42)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8264388', 'POINT(39.25917 -86.58611)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4109295', 'POINT(39.06194 -86.49806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114094', 'POINT(39.2697 -86.5483)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8259074', 'POINT(39.06194 -86.49806)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4117737', 'POINT(39.05944 -86.50667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8267410', 'POINT(39.05944 -86.50667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4118078', 'POINT(39.23 -86.4161)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4099309', 'POINT(39.122219 -86.397779)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8252873', 'POINT(39.12222 -86.39778)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045236', 'POINT(39.2325 -86.41528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224754', 'POINT(39.2325 -86.41528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4115078', 'POINT(39.1503 -86.3833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4107588', 'POINT(39.24611 -86.424999)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4118041', 'POINT(39.0608 -86.5808)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4119648', 'POINT(39.28028 -86.52583)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8269309', 'POINT(39.28028 -86.52583)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('10718152', 'POINT(39.2802 -86.5252)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049370', 'POINT(39.24861 -86.42278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8228832', 'POINT(39.24861 -86.42278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4119456', 'POINT(39.26222 -86.4375)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8269117', 'POINT(39.26222 -86.4375)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4115366', 'POINT(39.17083 -86.68056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8012587', 'POINT(39.2833 -86.5)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8265071', 'POINT(39.17083 -86.68056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4101696', 'POINT(39.1742 -86.6844)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114599', 'POINT(39.06833 -86.62528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048241', 'POINT(39.215 -86.67278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8227712', 'POINT(39.215 -86.67278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8264318', 'POINT(39.06833 -86.62528)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4050313', 'POINT(39.1086 -86.6697)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4106111', 'POINT(39.29611 -86.51889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4113325', 'POINT(39.26833 -86.63028)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8263060', 'POINT(39.26833 -86.63028)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4107747', 'POINT(39.03361 -86.51556)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4119349', 'POINT(39.0897 -86.6667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4051089', 'POINT(39.29861 -86.53694)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4044961', 'POINT(39.17694 -86.6975)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224486', 'POINT(39.17694 -86.6975)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8230538', 'POINT(39.29861 -86.53694)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046554', 'POINT(39.09278 -86.67278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4043280', 'POINT(39.22111 -86.684169)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048156', 'POINT(39.26 -86.64917)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8226054', 'POINT(39.09278 -86.67278)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8227627', 'POINT(39.26 -86.64917)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8222832', 'POINT(39.22111 -86.68417)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4099996', 'POINT(39.03306 -86.49056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8253545', 'POINT(39.03306 -86.49056)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4046859', 'POINT(39.0458 -86.6131)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8012470', 'POINT(39.0345 -86.4644)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8028078', 'POINT(39.25 -86.3833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049035', 'POINT(39.1519 -86.3472)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4047692', 'POINT(39.06694 -86.65444)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8227171', 'POINT(39.06694 -86.65444)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4042787', 'POINT(39.1361 -86.7039)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045208', 'POINT(39.18194 -86.70667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224727', 'POINT(39.18194 -86.70667)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048256', 'POINT(39.02333 -86.5325)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8227727', 'POINT(39.02333 -86.532499)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('5797367', 'POINT(39.24222 -86.68333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049874', 'POINT(39.29778 -86.59778)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8229334', 'POINT(39.29778 -86.59778)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8680755', 'POINT(39.24222 -86.68333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4117175', 'POINT(39.03861 -86.61583)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4043266', 'POINT(39.13556 -86.70833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045251', 'POINT(39.1475 -86.71083)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8266851', 'POINT(39.03861 -86.61583)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8222819', 'POINT(39.135559 -86.70833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224769', 'POINT(39.1475 -86.71083)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045020', 'POINT(39.08694 -86.68361)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224545', 'POINT(39.08694 -86.68361)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4100496', 'POINT(39.05444 -86.40194)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4111543', 'POINT(39.2596 -86.3811)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4050982', 'POINT(39.2825 -86.4125)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4042734', 'POINT(39.3081 -86.4833)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8254041', 'POINT(39.05444 -86.40194)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048660', 'POINT(39.22417 -86.70167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8228131', 'POINT(39.22417 -86.70167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4107598', 'POINT(39.29611 -86.62167)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4048235', 'POINT(39.01583 -86.54722)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8227706', 'POINT(39.01583 -86.547219)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4042825', 'POINT(39.0133 -86.545)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114276', 'POINT(39.24889 -86.69333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4114654', 'POINT(39.24194 -86.69861)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4116548', 'POINT(39.12333 -86.71889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4045307', 'POINT(39.198329 -86.721109)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('4049864', 'POINT(39.09194 -86.70111)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8266236', 'POINT(39.12333 -86.71889)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8229324', 'POINT(39.09194 -86.701109)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8224824', 'POINT(39.19833 -86.72111)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8263997', 'POINT(39.24889 -86.69333)')");
        INSERT_REPS.add("INSERT INTO hhs.rep_location_search(rep_id, lat_long) VALUES('8264373', 'POINT(39.24194 -86.69861)')");
    }
}
