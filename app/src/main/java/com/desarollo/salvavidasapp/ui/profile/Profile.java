package com.desarollo.salvavidasapp.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Spinner municipio;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        TextView UserName = view.findViewById(R.id.nombre_perfil);
        TextView UserMail = view.findViewById(R.id.correo_perfil);
        ImageView UserPhoto = view.findViewById(R.id.foto_perfil);
        EditText nombres = view.findViewById(R.id.tv_nombre);
        EditText apellidos = view.findViewById(R.id.tv_apellido);
        EditText direccion = view.findViewById(R.id.tv_direccion);
        municipio = view.findViewById(R.id.sp_municipio);
        String [] municipios = {"Municipio/Departamento","MEDELLIN/Antioquia", "ABEJORRAL/Antioquia", "ABRIAQUI/Antioquia", "ALEJANDRIA/Antioquia",
                "AMAGA/Antioquia", "AMALFI/Antioquia", "ANDES/Antioquia", "ANGELOPOLIS/Antioquia", "ANGOSTURA/Antioquia", "ANORI/Antioquia",
                "SANTAFE DE ANTIOQUIA/Antioquia", "ANZA/Antioquia", "APARTADO/Antioquia", "ARBOLETES/Antioquia", "ARGELIA/Antioquia", "ARMENIA/Antioquia",
                "BARBOSA/Antioquia", "BELMIRA/Antioquia", "BELLO/Antioquia", "BETANIA/Antioquia", "BETULIA/Antioquia", "CIUDAD BOLIVAR/Antioquia",
                "BRICEÑO/Antioquia", "BURITICA/Antioquia", "CACERES/Antioquia", "CAICEDO/Antioquia", "CALDAS/Antioquia", "CAMPAMENTO/Antioquia",
                "CAÑASGORDAS/Antioquia", "CARACOLI/Antioquia", "CARAMANTA/Antioquia", "CAREPA/Antioquia", "EL CARMEN DE VIBORAL/Antioquia", "CAROLINA/Antioquia",
                "CAUCASIA/Antioquia", "CHIGORODO/Antioquia", "CISNEROS/Antioquia", "COCORNA/Antioquia", "CONCEPCION/Antioquia", "CONCORDIA/Antioquia",
                "COPACABANA/Antioquia", "DABEIBA/Antioquia", "DON MATIAS/Antioquia", "EBEJICO/Antioquia", "EL BAGRE/Antioquia", "ENTRERRIOS/Antioquia",
                "ENVIGADO/Antioquia", "FREDONIA/Antioquia", "FRONTINO/Antioquia", "GIRALDO/Antioquia", "GIRARDOTA/Antioquia", "GOMEZ PLATA/Antioquia",
                "GRANADA/Antioquia", "GUADALUPE/Antioquia", "GUARNE/Antioquia", "GUATAPE/Antioquia", "HELICONIA/Antioquia", "HISPANIA/Antioquia", "ITAGUI/Antioquia",
                "ITUANGO/Antioquia", "JARDIN/Antioquia", "JERICO/Antioquia", "LA CEJA/Antioquia", "LA ESTRELLA/Antioquia", "LA PINTADA/Antioquia", "LA UNION/Antioquia",
                "LIBORINA/Antioquia", "MACEO/Antioquia", "MARINILLA/Antioquia", "MONTEBELLO/Antioquia", "MURINDO/Antioquia", "MUTATA/Antioquia", "NARIÑO/Antioquia",
                "NECOCLI/Antioquia", "NECHI/Antioquia", "OLAYA/Antioquia", "PEÑOL/Antioquia", "PEQUE/Antioquia", "PUEBLORRICO/Antioquia", "PUERTO BERRIO/Antioquia",
                "PUERTO NARE/Antioquia", "PUERTO TRIUNFO/Antioquia", "REMEDIOS/Antioquia", "RETIRO/Antioquia", "RIONEGRO/Antioquia", "SABANALARGA/Antioquia",
                "SABANETA/Antioquia", "SALGAR/Antioquia", "SAN ANDRES/Antioquia", "SAN CARLOS/Antioquia", "SAN FRANCISCO/Antioquia", "SAN JERONIMO/Antioquia",
                "SAN JOSE DE LA MONTAÑA/Antioquia", "SAN JUAN DE URABA/Antioquia", "SAN LUIS/Antioquia", "SAN PEDRO/Antioquia", "SAN PEDRO DE URABA/Antioquia",
                "SAN RAFAEL/Antioquia", "SAN ROQUE/Antioquia", "SAN VICENTE/Antioquia", "SANTA BARBARA/Antioquia", "SANTA ROSA DE OSOS/Antioquia",
                "SANTO DOMINGO/Antioquia", "EL SANTUARIO/Antioquia", "SEGOVIA/Antioquia", "SONSON/Antioquia", "SOPETRAN/Antioquia", "TAMESIS/Antioquia",
                "TARAZA/Antioquia", "TARSO/Antioquia", "TITIRIBI/Antioquia", "TOLEDO/Antioquia", "TURBO/Antioquia", "URAMITA/Antioquia", "URRAO/Antioquia",
                "VALDIVIA/Antioquia", "VALPARAISO/Antioquia", "VEGACHI/Antioquia", "VENECIA/Antioquia", "VIGIA DEL FUERTE/Antioquia", "YALI/Antioquia", "YARUMAL/Antioquia",
                "YOLOMBO/Antioquia", "YONDO/Antioquia", "ZARAGOZA/Antioquia", "BARRANQUILLA/Atlántico", "BARANOA/Atlántico", "CAMPO DE LA CRUZ/Atlántico",
                "CANDELARIA/Atlántico", "GALAPA/Atlántico", "JUAN DE ACOSTA/Atlántico", "LURUACO/Atlántico", "MALAMBO/Atlántico", "MANATI/Atlántico",
                "PALMAR DE VARELA/Atlántico", "PIOJO/Atlántico", "POLONUEVO/Atlántico", "PONEDERA/Atlántico", "PUERTO COLOMBIA/Atlántico", "REPELON/Atlántico",
                "SABANAGRANDE/Atlántico", "SABANALARGA/Atlántico", "SANTA LUCIA/Atlántico", "SANTO TOMAS/Atlántico", "SOLEDAD/Atlántico", "SUAN/Atlántico",
                "TUBARA/Atlántico", "USIACURI/Atlántico", "BOGOTÁ/Bogotá D.C", "CARTAGENA/Bolívar", "ACHI/Bolívar", "ALTOS DEL ROSARIO/Bolívar", "ARENAL/Bolívar",
                "ARJONA/Bolívar", "ARROYOHONDO/Bolívar", "BARRANCO DE LOBA/Bolívar", "CALAMAR/Bolívar", "CANTAGALLO/Bolívar", "CICUCO/Bolívar", "CORDOBA/Bolívar",
                "CLEMENCIA/Bolívar", "EL CARMEN DE BOLIVAR/Bolívar", "EL GUAMO/Bolívar", "EL PEÑON/Bolívar", "HATILLO DE LOBA/Bolívar", "MAGANGUE/Bolívar",
                "MAHATES/Bolívar", "MARGARITA/Bolívar", "MARIA LA BAJA/Bolívar", "MONTECRISTO/Bolívar", "MOMPOS/Bolívar", "MORALES/Bolívar", "PINILLOS/Bolívar",
                "REGIDOR/Bolívar", "RIO VIEJO/Bolívar", "SAN CRISTOBAL/Bolívar", "SAN ESTANISLAO/Bolívar", "SAN FERNANDO/Bolívar", "SAN JACINTO/Bolívar",
                "SAN JACINTO DEL CAUCA/Bolívar", "SAN JUAN NEPOMUCENO/Bolívar", "SAN MARTIN DE LOBA/Bolívar", "SAN PABLO/Bolívar", "SANTA CATALINA/Bolívar",
                "SANTA ROSA/Bolívar", "SANTA ROSA DEL SUR/Bolívar", "SIMITI/Bolívar", "SOPLAVIENTO/Bolívar", "TALAIGUA NUEVO/Bolívar", "TIQUISIO/Bolívar",
                "TURBACO/Bolívar", "TURBANA/Bolívar", "VILLANUEVA/Bolívar", "ZAMBRANO/Bolívar", "TUNJA/Boyacá", "ALMEIDA/Boyacá", "AQUITANIA/Boyacá",
                "ARCABUCO/Boyacá", "BELEN/Boyacá", "BERBEO/Boyacá", "BETEITIVA/Boyacá", "BOAVITA/Boyacá", "BOYACA/Boyacá", "BRICEÑO/Boyacá", "BUENAVISTA/Boyacá",
                "BUSBANZA/Boyacá", "CALDAS/Boyacá", "CAMPOHERMOSO/Boyacá", "CERINZA/Boyacá", "CHINAVITA/Boyacá", "CHIQUINQUIRA/Boyacá", "CHISCAS/Boyacá",
                "CHITA/Boyacá", "CHITARAQUE/Boyacá", "CHIVATA/Boyacá", "CIENEGA/Boyacá", "COMBITA/Boyacá", "COPER/Boyacá", "CORRALES/Boyacá", "COVARACHIA/Boyacá",
                "CUBARA/Boyacá", "CUCAITA/Boyacá", "CUITIVA/Boyacá", "CHIQUIZA/Boyacá", "CHIVOR/Boyacá", "DUITAMA/Boyacá", "EL COCUY/Boyacá", "EL ESPINO/Boyacá",
                "FIRAVITOBA/Boyacá", "FLORESTA/Boyacá", "GACHANTIVA/Boyacá", "GAMEZA/Boyacá", "GARAGOA/Boyacá", "GUACAMAYAS/Boyacá", "GUATEQUE/Boyacá", "GUAYATA/Boyacá",
                "GÜICAN/Boyacá", "IZA/Boyacá", "JENESANO/Boyacá", "JERICO/Boyacá", "LABRANZAGRANDE/Boyacá", "LA CAPILLA/Boyacá", "LA VICTORIA/Boyacá", "LA UVITA/Boyacá",
                "VILLA DE LEYVA/Boyacá", "MACANAL/Boyacá", "MARIPI/Boyacá", "MIRAFLORES/Boyacá", "MONGUA/Boyacá", "MONGUI/Boyacá", "MONIQUIRA/Boyacá", "MOTAVITA/Boyacá",
                "MUZO/Boyacá", "NOBSA/Boyacá", "NUEVO COLON/Boyacá", "OICATA/Boyacá", "OTANCHE/Boyacá", "PACHAVITA/Boyacá", "PAEZ/Boyacá", "PAIPA/Boyacá",
                "PAJARITO/Boyacá", "PANQUEBA/Boyacá", "PAUNA/Boyacá", "PAYA/Boyacá", "PAZ DE RIO/Boyacá", "PESCA/Boyacá", "PISBA/Boyacá", "PUERTO BOYACA/Boyacá",
                "QUIPAMA/Boyacá", "RAMIRIQUI/Boyacá", "RAQUIRA/Boyacá", "RONDON/Boyacá", "SABOYA/Boyacá", "SACHICA/Boyacá", "SAMACA/Boyacá", "SAN EDUARDO/Boyacá",
                "SAN JOSE DE PARE/Boyacá", "SAN LUIS DE GACENO/Boyacá", "SAN MATEO/Boyacá", "SAN MIGUEL DE SEMA/Boyacá", "SAN PABLO DE BORBUR/Boyacá", "SANTANA/Boyacá",
                "SANTA MARIA/Boyacá", "SANTA ROSA DE VITERBO/Boyacá", "SANTA SOFIA/Boyacá", "SATIVANORTE/Boyacá", "SATIVASUR/Boyacá", "SIACHOQUE/Boyacá", "SOATA/Boyacá",
                "SOCOTA/Boyacá", "SOCHA/Boyacá", "SOGAMOSO/Boyacá", "SOMONDOCO/Boyacá", "SORA/Boyacá", "SOTAQUIRA/Boyacá", "SORACA/Boyacá", "SUSACON/Boyacá",
                "SUTAMARCHAN/Boyacá", "SUTATENZA/Boyacá", "TASCO/Boyacá", "TENZA/Boyacá", "TIBANA/Boyacá", "TIBASOSA/Boyacá", "TINJACA/Boyacá", "TIPACOQUE/Boyacá",
                "TOCA/Boyacá", "TOGÜI/Boyacá", "TOPAGA/Boyacá", "TOTA/Boyacá", "TUNUNGUA/Boyacá", "TURMEQUE/Boyacá", "TUTA/Boyacá", "TUTAZA/Boyacá", "UMBITA/Boyacá",
                "VENTAQUEMADA/Boyacá", "VIRACACHA/Boyacá", "ZETAQUIRA/Boyacá", "MANIZALES/Caldas", "AGUADAS/Caldas", "ANSERMA/Caldas", "ARANZAZU/Caldas", "BELALCAZAR/Caldas",
                "CHINCHINA/Caldas", "FILADELFIA/Caldas", "LA DORADA/Caldas", "LA MERCED/Caldas", "MANZANARES/Caldas", "MARMATO/Caldas", "MARQUETALIA/Caldas",
                "MARULANDA/Caldas", "NEIRA/Caldas", "NORCASIA/Caldas", "PACORA/Caldas", "PALESTINA/Caldas", "PENSILVANIA/Caldas", "RIOSUCIO/Caldas", "RISARALDA/Caldas",
                "SALAMINA/Caldas", "SAMANA/Caldas", "SAN JOSE/Caldas", "SUPIA/Caldas", "VICTORIA/Caldas", "VILLAMARIA/Caldas", "VITERBO/Caldas", "FLORENCIA/Caquetá",
                "ALBANIA/Caquetá", "BELEN DE LOS ANDAQUIES/Caquetá", "CARTAGENA DEL CHAIRA/Caquetá", "CURILLO/Caquetá", "EL DONCELLO/Caquetá", "EL PAUJIL/Caquetá",
                "LA MONTAÑITA/Caquetá", "MILAN/Caquetá", "MORELIA/Caquetá", "PUERTO RICO/Caquetá", "SAN JOSE DEL FRAGUA/Caquetá", "SAN VICENTE DEL CAGUAN/Caquetá",
                "SOLANO/Caquetá", "SOLITA/Caquetá", "VALPARAISO/Caquetá", "POPAYAN/Cauca", "ALMAGUER/Cauca", "ARGELIA/Cauca", "BALBOA/Cauca", "BOLIVAR/Cauca",
                "BUENOS AIRES/Cauca", "CAJIBIO/Cauca", "CALDONO/Cauca", "CALOTO/Cauca", "CORINTO/Cauca", "EL TAMBO/Cauca", "FLORENCIA/Cauca", "GUAPI/Cauca", "INZA/Cauca",
                "JAMBALO/Cauca", "LA SIERRA/Cauca", "LA VEGA/Cauca", "LOPEZ/Cauca", "MERCADERES/Cauca", "MIRANDA/Cauca", "MORALES/Cauca", "PADILLA/Cauca", "PAEZ/Cauca",
                "PATIA/Cauca", "PIAMONTE/Cauca", "PIENDAMO/Cauca", "PUERTO TEJADA/Cauca", "PURACE/Cauca", "ROSAS/Cauca", "SAN SEBASTIAN/Cauca", "SANTANDER DE QUILICHAO/Cauca",
                "SANTA ROSA/Cauca", "SILVIA/Cauca", "SOTARA/Cauca", "SUAREZ/Cauca", "SUCRE/Cauca", "TIMBIO/Cauca", "TIMBIQUI/Cauca", "TORIBIO/Cauca", "TOTORO/Cauca",
                "VILLA RICA/Cauca", "VALLEDUPAR/Cesar", "AGUACHICA/Cesar", "AGUSTIN CODAZZI/Cesar", "ASTREA/Cesar", "BECERRIL/Cesar", "BOSCONIA/Cesar", "CHIMICHAGUA/Cesar",
                "CHIRIGUANA/Cesar", "CURUMANI/Cesar", "EL COPEY/Cesar", "EL PASO/Cesar", "GAMARRA/Cesar", "GONZALEZ/Cesar", "LA GLORIA/Cesar", "LA JAGUA DE IBIRICO/Cesar",
                "MANAURE/Cesar", "PAILITAS/Cesar", "PELAYA/Cesar", "PUEBLO BELLO/Cesar", "RIO DE ORO/Cesar", "LA PAZ/Cesar", "SAN ALBERTO/Cesar", "SAN DIEGO/Cesar",
                "SAN MARTIN/Cesar", "TAMALAMEQUE/Cesar", "MONTERIA/Córdoba", "AYAPEL/Córdoba", "BUENAVISTA/Córdoba", "CANALETE/Córdoba", "CERETE/Córdoba", "CHIMA/Córdoba",
                "CHINU/Córdoba", "CIENAGA DE ORO/Córdoba", "COTORRA/Córdoba", "LA APARTADA/Córdoba", "LORICA/Córdoba", "LOS CORDOBAS/Córdoba", "MOMIL/Córdoba",
                "MONTELIBANO/Córdoba", "MOÑITOS/Córdoba", "PLANETA RICA/Córdoba", "PUEBLO NUEVO/Córdoba", "PUERTO ESCONDIDO/Córdoba", "PUERTO LIBERTADOR/Córdoba",
                "PURISIMA/Córdoba", "SAHAG/N/Córdoba", "SAN ANDRES SOTAVENTO/Córdoba", "SAN ANTERO/Córdoba", "SAN BERNARDO DEL VIENTO/Córdoba", "SAN CARLOS/Córdoba",
                "SAN PELAYO/Córdoba", "TIERRALTA/Córdoba", "VALENCIA/Córdoba", "AGUA DE DIOS/Cundinamarca", "ALBAN/Cundinamarca", "ANAPOIMA/Cundinamarca",
                "ANOLAIMA/Cundinamarca", "ARBELAEZ/Cundinamarca", "BELTRAN/Cundinamarca", "BITUIMA/Cundinamarca", "BOJACA/Cundinamarca", "CABRERA/Cundinamarca",
                "CACHIPAY/Cundinamarca", "CAJICA/Cundinamarca", "CAPARRAPI/Cundinamarca", "CAQUEZA/Cundinamarca", "CARMEN DE CARUPA/Cundinamarca", "CHAGUANI/Cundinamarca",
                "CHIA/Cundinamarca", "CHIPAQUE/Cundinamarca", "CHOACHI/Cundinamarca", "CHOCONTA/Cundinamarca", "COGUA/Cundinamarca", "COTA/Cundinamarca",
                "CUCUNUBA/Cundinamarca", "EL COLEGIO/Cundinamarca", "EL PEÑON/Cundinamarca", "EL ROSAL/Cundinamarca", "FACATATIVA/Cundinamarca", "FOMEQUE/Cundinamarca",
                "FOSCA/Cundinamarca", "FUNZA/Cundinamarca", "F/QUENE/Cundinamarca", "FUSAGASUGA/Cundinamarca", "GACHALA/Cundinamarca", "GACHANCIPA/Cundinamarca",
                "GACHETA/Cundinamarca", "GAMA/Cundinamarca", "GIRARDOT/Cundinamarca", "GRANADA/Cundinamarca", "GUACHETA/Cundinamarca", "GUADUAS/Cundinamarca",
                "GUASCA/Cundinamarca", "GUATAQUI/Cundinamarca", "GUATAVITA/Cundinamarca", "GUAYABAL DE SIQUIMA/Cundinamarca", "GUAYABETAL/Cundinamarca",
                "GUTIERREZ/Cundinamarca", "JERUSALEN/Cundinamarca", "JUNIN/Cundinamarca", "LA CALERA/Cundinamarca", "LA MESA/Cundinamarca", "LA PALMA/Cundinamarca",
                "LA PEÑA/Cundinamarca", "LA VEGA/Cundinamarca", "LENGUAZAQUE/Cundinamarca", "MACHETA/Cundinamarca", "MADRID/Cundinamarca", "MANTA/Cundinamarca",
                "MEDINA/Cundinamarca", "MOSQUERA/Cundinamarca", "NARIÑO/Cundinamarca", "NEMOCON/Cundinamarca", "NILO/Cundinamarca", "NIMAIMA/Cundinamarca",
                "NOCAIMA/Cundinamarca", "VENECIA/Cundinamarca", "PACHO/Cundinamarca", "PAIME/Cundinamarca", "PANDI/Cundinamarca", "PARATEBUENO/Cundinamarca",
                "PASCA/Cundinamarca", "PUERTO SALGAR/Cundinamarca", "PULI/Cundinamarca", "QUEBRADANEGRA/Cundinamarca", "QUETAME/Cundinamarca", "QUIPILE/Cundinamarca",
                "APULO/Cundinamarca", "RICAURTE/Cundinamarca", "SAN ANTONIO DEL TEQUENDAMA/Cundinamarca", "SAN BERNARDO/Cundinamarca", "SAN CAYETANO/Cundinamarca",
                "SAN FRANCISCO/Cundinamarca", "SAN JUAN DE RIO SECO/Cundinamarca", "SASAIMA/Cundinamarca", "SESQUILE/Cundinamarca", "SIBATE/Cundinamarca",
                "SILVANIA/Cundinamarca", "SIMIJACA/Cundinamarca", "SOACHA/Cundinamarca", "SOPO/Cundinamarca", "SUBACHOQUE/Cundinamarca", "SUESCA/Cundinamarca",
                "SUPATA/Cundinamarca", "SUSA/Cundinamarca", "SUTATAUSA/Cundinamarca", "TABIO/Cundinamarca", "TAUSA/Cundinamarca", "TENA/Cundinamarca",
                "TENJO/Cundinamarca", "TIBACUY/Cundinamarca", "TIBIRITA/Cundinamarca", "TOCAIMA/Cundinamarca", "TOCANCIPA/Cundinamarca", "TOPAIPI/Cundinamarca",
                "UBALA/Cundinamarca", "UBAQUE/Cundinamarca", "VILLA DE SAN DIEGO DE UBATE/Cundinamarca", "UNE/Cundinamarca", "UTICA/Cundinamarca", "VERGARA/Cundinamarca",
                "VIANI/Cundinamarca", "VILLAGOMEZ/Cundinamarca", "VILLAPINZON/Cundinamarca", "VILLETA/Cundinamarca", "VIOTA/Cundinamarca", "YACOPI/Cundinamarca",
                "ZIPACON/Cundinamarca", "ZIPAQUIRA/Cundinamarca", "QUIBDO/Chocó", "ACANDI/Chocó", "ALTO BAUDO/Chocó", "ATRATO/Chocó", "BAGADO/Chocó",
                "BAHIA SOLANO/Chocó", "BAJO BAUDO/Chocó", "BELEN DE BAJIRA/Chocó", "BOJAYA/Chocó", "EL CANTON DEL SAN PABLO/Chocó", "CARMEN DEL DARIEN/Chocó",
                "CERTEGUI/Chocó", "CONDOTO/Chocó", "EL CARMEN DE ATRATO/Chocó", "EL LITORAL DEL SAN JUAN/Chocó", "ISTMINA/Chocó", "JURADO/Chocó", "LLORO/Chocó",
                "MEDIO ATRATO/Chocó", "MEDIO BAUDO/Chocó", "MEDIO SAN JUAN/Chocó", "NOVITA/Chocó", "NUQUI/Chocó", "RIO IRO/Chocó", "RIO QUITO/Chocó", "RIOSUCIO/Chocó",
                "SAN JOSE DEL PALMAR/Chocó", "SIPI/Chocó", "TADO/Chocó", "UNGUIA/Chocó", "UNION PANAMERICANA/Chocó", "NEIVA/Huila", "ACEVEDO/Huila", "AGRADO/Huila",
                "AIPE/Huila", "ALGECIRAS/Huila", "ALTAMIRA/Huila", "BARAYA/Huila", "CAMPOALEGRE/Huila", "COLOMBIA/Huila", "ELIAS/Huila", "GARZON/Huila",
                "GIGANTE/Huila", "GUADALUPE/Huila", "HOBO/Huila", "IQUIRA/Huila", "ISNOS/Huila", "LA ARGENTINA/Huila", "LA PLATA/Huila", "NATAGA/Huila",
                "OPORAPA/Huila", "PAICOL/Huila", "PALERMO/Huila", "PALESTINA/Huila", "PITAL/Huila", "PITALITO/Huila", "RIVERA/Huila", "SALADOBLANCO/Huila",
                "SAN AGUSTIN/Huila", "SANTA MARIA/Huila", "SUAZA/Huila", "TARQUI/Huila", "TESALIA/Huila", "TELLO/Huila", "TERUEL/Huila", "TIMANA/Huila", "VILLAVIEJA/Huila",
                "YAGUARA/Huila", "RIOHACHA/La Guajira", "ALBANIA/La Guajira", "BARRANCAS/La Guajira", "DIBULLA/La Guajira", "DISTRACCION/La Guajira", "EL MOLINO/La Guajira",
                "FONSECA/La Guajira", "HATONUEVO/La Guajira", "LA JAGUA DEL PILAR/La Guajira", "MAICAO/La Guajira", "MANAURE/La Guajira", "SAN JUAN DEL CESAR/La Guajira",
                "URIBIA/La Guajira", "URUMITA/La Guajira", "VILLANUEVA/La Guajira", "SANTA MARTA/Magdalena", "ALGARROBO/Magdalena", "ARACATACA/Magdalena",
                "ARIGUANI/Magdalena", "CERRO SAN ANTONIO/Magdalena", "CHIBOLO/Magdalena", "CIENAGA/Magdalena", "CONCORDIA/Magdalena", "EL BANCO/Magdalena",
                "EL PIÑON/Magdalena", "EL RETEN/Magdalena", "FUNDACION/Magdalena", "GUAMAL/Magdalena", "NUEVA GRANADA/Magdalena", "PEDRAZA/Magdalena",
                "PIJIÑO DEL CARMEN/Magdalena", "PIVIJAY/Magdalena", "PLATO/Magdalena", "PUEBLOVIEJO/Magdalena", "REMOLINO/Magdalena", "SABANAS DE SAN ANGEL/Magdalena",
                "SALAMINA/Magdalena", "SAN SEBASTIAN DE BUENAVISTA/Magdalena", "SAN ZENON/Magdalena", "SANTA ANA/Magdalena", "SANTA BARBARA DE PINTO/Magdalena",
                "SITIONUEVO/Magdalena", "TENERIFE/Magdalena", "ZAPAYAN/Magdalena", "ZONA BANANERA/Magdalena", "VILLAVICENCIO/Meta", "ACACIAS/Meta", "BARRANCA DE UPIA/Meta",
                "CABUYARO/Meta", "CASTILLA LA NUEVA/Meta", "CUBARRAL/Meta", "CUMARAL/Meta", "EL CALVARIO/Meta", "EL CASTILLO/Meta", "EL DORADO/Meta", "FUENTE DE ORO/Meta",
                "GRANADA/Meta", "GUAMAL/Meta", "MAPIRIPAN/Meta", "MESETAS/Meta", "LA MACARENA/Meta", "URIBE/Meta", "LEJANIAS/Meta", "PUERTO CONCORDIA/Meta",
                "PUERTO GAITAN/Meta", "PUERTO LOPEZ/Meta", "PUERTO LLERAS/Meta", "PUERTO RICO/Meta", "RESTREPO/Meta", "SAN CARLOS DE GUAROA/Meta",
                "SAN JUAN DE ARAMA/Meta", "SAN JUANITO/Meta", "SAN MARTIN/Meta", "VISTAHERMOSA/Meta", "PASTO/Nariño", "ALBAN/Nariño", "ALDANA/Nariño",
                "ANCUYA/Nariño", "ARBOLEDA/Nariño", "BARBACOAS/Nariño", "BELEN/Nariño", "BUESACO/Nariño", "COLON/Nariño", "CONSACA/Nariño", "CONTADERO/Nariño",
                "CORDOBA/Nariño", "CUASPUD/Nariño", "CUMBAL/Nariño", "CUMBITARA/Nariño", "CHACHAGÜI/Nariño", "EL CHARCO/Nariño", "EL PEÑOL/Nariño", "EL ROSARIO/Nariño",
                "EL TABLON DE GOMEZ/Nariño", "EL TAMBO/Nariño", "FUNES/Nariño", "GUACHUCAL/Nariño", "GUAITARILLA/Nariño", "GUALMATAN/Nariño", "ILES/Nariño", "IMUES/Nariño",
                "IPIALES/Nariño", "LA CRUZ/Nariño", "LA FLORIDA/Nariño", "LA LLANADA/Nariño", "LA TOLA/Nariño", "LA UNION/Nariño", "LEIVA/Nariño", "LINARES/Nariño",
                "LOS ANDES/Nariño", "MAGÜI/Nariño", "MALLAMA/Nariño", "MOSQUERA/Nariño", "NARIÑO/Nariño", "OLAYA HERRERA/Nariño", "OSPINA/Nariño", "FRANCISCO PIZARRO/Nariño",
                "POLICARPA/Nariño", "POTOSI/Nariño", "PROVIDENCIA/Nariño", "PUERRES/Nariño", "PUPIALES/Nariño", "RICAURTE/Nariño", "ROBERTO PAYAN/Nariño", "SAMANIEGO/Nariño",
                "SANDONA/Nariño", "SAN BERNARDO/Nariño", "SAN LORENZO/Nariño", "SAN PABLO/Nariño", "SAN PEDRO DE CARTAGO/Nariño", "SANTA BARBARA/Nariño", "SANTACRUZ/Nariño",
                "SAPUYES/Nariño", "TAMINANGO/Nariño", "TANGUA/Nariño", "TUMACO/Nariño", "TUQUERRES/Nariño", "YACUANQUER/Nariño", "CUCUTA/Nte de Santander",
                "ABREGO/Nte de Santander", "ARBOLEDAS/Nte de Santander", "BOCHALEMA/Nte de Santander", "BUCARASICA/Nte de Santander", "CACOTA/Nte de Santander",
                "CACHIRA/Nte de Santander", "CHINACOTA/Nte de Santander", "CHITAGA/Nte de Santander", "CONVENCION/Nte de Santander", "CUCUTILLA/Nte de Santander",
                "DURANIA/Nte de Santander", "EL CARMEN/Nte de Santander", "EL TARRA/Nte de Santander", "EL ZULIA/Nte de Santander", "GRAMALOTE/Nte de Santander",
                "HACARI/Nte de Santander", "HERRAN/Nte de Santander", "LABATECA/Nte de Santander", "LA ESPERANZA/Nte de Santander", "LA PLAYA/Nte de Santander",
                "LOS PATIOS/Nte de Santander", "LOURDES/Nte de Santander", "MUTISCUA/Nte de Santander", "OCAÑA/Nte de Santander", "PAMPLONA/Nte de Santander",
                "PAMPLONITA/Nte de Santander", "PUERTO SANTANDER/Nte de Santander", "RAGONVALIA/Nte de Santander", "SALAZAR/Nte de Santander", "SAN CALIXTO/Nte de Santander",
                "SAN CAYETANO/Nte de Santander", "SANTIAGO/Nte de Santander", "SARDINATA/Nte de Santander", "SILOS/Nte de Santander", "TEORAMA/Nte de Santander",
                "TIBU/Nte de Santander", "TOLEDO/Nte de Santander", "VILLA CARO/Nte de Santander", "VILLA DEL ROSARIO/Nte de Santander", "ARMENIA/Quindio",
                "BUENAVISTA/Quindio", "CALARCA/Quindio", "CIRCASIA/Quindio", "CORDOBA/Quindio", "FILANDIA/Quindio", "GENOVA/Quindio", "LA TEBAIDA/Quindio",
                "MONTENEGRO/Quindio", "PIJAO/Quindio", "QUIMBAYA/Quindio", "SALENTO/Quindio", "PEREIRA/Risaralda", "APIA/Risaralda", "BALBOA/Risaralda",
                "BELEN DE UMBRIA/Risaralda", "DOSQUEBRADAS/Risaralda", "GUATICA/Risaralda", "LA CELIA/Risaralda", "LA VIRGINIA/Risaralda", "MARSELLA/Risaralda",
                "MISTRATO/Risaralda", "PUEBLO RICO/Risaralda", "QUINCHIA/Risaralda", "SANTA ROSA DE CABAL/Risaralda", "SANTUARIO/Risaralda", "BUCARAMANGA/Santander",
                "AGUADA/Santander", "ALBANIA/Santander", "ARATOCA/Santander", "BARBOSA/Santander", "BARICHARA/Santander", "BARRANCABERMEJA/Santander", "BETULIA/Santander",
                "BOLIVAR/Santander", "CABRERA/Santander", "CALIFORNIA/Santander", "CAPITANEJO/Santander", "CARCASI/Santander", "CEPITA/Santander", "CERRITO/Santander",
                "CHARALA/Santander", "CHARTA/Santander", "CHIMA/Santander", "CHIPATA/Santander", "CIMITARRA/Santander", "CONCEPCION/Santander", "CONFINES/Santander",
                "CONTRATACION/Santander", "COROMORO/Santander", "CURITI/Santander", "EL CARMEN DE CHUCURI/Santander", "EL GUACAMAYO/Santander", "EL PEÑON/Santander",
                "EL PLAYON/Santander", "ENCINO/Santander", "ENCISO/Santander", "FLORIAN/Santander", "FLORIDABLANCA/Santander", "GALAN/Santander", "GAMBITA/Santander",
                "GIRON/Santander", "GUACA/Santander", "GUADALUPE/Santander", "GUAPOTA/Santander", "GUAVATA/Santander", "GÜEPSA/Santander", "HATO/Santander",
                "JES/S MARIA/Santander", "JORDAN/Santander", "LA BELLEZA/Santander", "LANDAZURI/Santander", "LA PAZ/Santander", "LEBRIJA/Santander", "LOS SANTOS/Santander",
                "MACARAVITA/Santander", "MALAGA/Santander", "MATANZA/Santander", "MOGOTES/Santander", "MOLAGAVITA/Santander", "OCAMONTE/Santander", "OIBA/Santander",
                "ONZAGA/Santander", "PALMAR/Santander", "PALMAS DEL SOCORRO/Santander", "PARAMO/Santander", "PIEDECUESTA/Santander", "PINCHOTE/Santander",
                "PUENTE NACIONAL/Santander", "PUERTO PARRA/Santander", "PUERTO WILCHES/Santander", "RIONEGRO/Santander", "SABANA DE TORRES/Santander",
                "SAN ANDRES/Santander", "SAN BENITO/Santander", "SAN GIL/Santander", "SAN JOAQUIN/Santander", "SAN JOSE DE MIRANDA/Santander", "SAN MIGUEL/Santander",
                "SAN VICENTE DE CHUCURI/Santander", "SANTA BARBARA/Santander", "SANTA HELENA DEL OPON/Santander", "SIMACOTA/Santander", "SOCORRO/Santander",
                "SUAITA/Santander", "SUCRE/Santander", "SURATA/Santander", "TONA/Santander", "VALLE DE SAN JOSE/Santander", "VELEZ/Santander", "VETAS/Santander",
                "VILLANUEVA/Santander", "ZAPATOCA/Santander", "SINCELEJO/Sucre", "BUENAVISTA/Sucre", "CAIMITO/Sucre", "COLOSO/Sucre", "COROZAL/Sucre", "COVEÑAS/Sucre",
                "CHALAN/Sucre", "EL ROBLE/Sucre", "GALERAS/Sucre", "GUARANDA/Sucre", "LA UNION/Sucre", "LOS PALMITOS/Sucre", "MAJAGUAL/Sucre", "MORROA/Sucre",
                "OVEJAS/Sucre", "PALMITO/Sucre", "SAMPUES/Sucre", "SAN BENITO ABAD/Sucre", "SAN JUAN DE BETULIA/Sucre", "SAN MARCOS/Sucre", "SAN ONOFRE/Sucre",
                "SAN PEDRO/Sucre", "SINCE/Sucre", "SUCRE/Sucre", "SANTIAGO DE TOLU/Sucre", "TOLUVIEJO/Sucre", "IBAGUE/Tolima", "ALPUJARRA/Tolima", "ALVARADO/Tolima",
                "AMBALEMA/Tolima", "ANZOATEGUI/Tolima", "ARMERO/Tolima", "ATACO/Tolima", "CAJAMARCA/Tolima", "CARMEN DE APICALA/Tolima", "CASABIANCA/Tolima",
                "CHAPARRAL/Tolima", "COELLO/Tolima", "COYAIMA/Tolima", "CUNDAY/Tolima", "DOLORES/Tolima", "ESPINAL/Tolima", "FALAN/Tolima", "FLANDES/Tolima",
                "FRESNO/Tolima", "GUAMO/Tolima", "HERVEO/Tolima", "HONDA/Tolima", "ICONONZO/Tolima", "LERIDA/Tolima", "LIBANO/Tolima", "MARIQUITA/Tolima", "MELGAR/Tolima",
                "MURILLO/Tolima", "NATAGAIMA/Tolima", "ORTEGA/Tolima", "PALOCABILDO/Tolima", "PIEDRAS/Tolima", "PLANADAS/Tolima", "PRADO/Tolima", "PURIFICACION/Tolima",
                "RIOBLANCO/Tolima", "RONCESVALLES/Tolima", "ROVIRA/Tolima", "SALDAÑA/Tolima", "SAN ANTONIO/Tolima", "SAN LUIS/Tolima", "SANTA ISABEL/Tolima",
                "SUAREZ/Tolima", "VALLE DE SAN JUAN/Tolima", "VENADILLO/Tolima", "VILLAHERMOSA/Tolima", "VILLARRICA/Tolima", "CALI/Valle del Cauca",
                "ALCALA/Valle del Cauca", "ANDALUCIA/Valle del Cauca", "ANSERMANUEVO/Valle del Cauca", "ARGELIA/Valle del Cauca", "BOLIVAR/Valle del Cauca",
                "BUENAVENTURA/Valle del Cauca", "GUADALAJARA DE BUGA/Valle del Cauca", "BUGALAGRANDE/Valle del Cauca", "CAICEDONIA/Valle del Cauca",
                "CALIMA/Valle del Cauca", "CANDELARIA/Valle del Cauca", "CARTAGO/Valle del Cauca", "DAGUA/Valle del Cauca", "EL AGUILA/Valle del Cauca",
                "EL CAIRO/Valle del Cauca", "EL CERRITO/Valle del Cauca", "EL DOVIO/Valle del Cauca", "FLORIDA/Valle del Cauca", "GINEBRA/Valle del Cauca",
                "GUACARI/Valle del Cauca", "JAMUNDI/Valle del Cauca", "LA CUMBRE/Valle del Cauca", "LA UNION/Valle del Cauca", "LA VICTORIA/Valle del Cauca",
                "OBANDO/Valle del Cauca", "PALMIRA/Valle del Cauca", "PRADERA/Valle del Cauca", "RESTREPO/Valle del Cauca", "RIOFRIO/Valle del Cauca",
                "ROLDANILLO/Valle del Cauca", "SAN PEDRO/Valle del Cauca", "SEVILLA/Valle del Cauca", "TORO/Valle del Cauca", "TRUJILLO/Valle del Cauca",
                "TULUA/Valle del Cauca", "ULLOA/Valle del Cauca", "VERSALLES/Valle del Cauca", "VIJES/Valle del Cauca", "YOTOCO/Valle del Cauca", "YUMBO/Valle del Cauca",
                "ZARZAL/Valle del Cauca", "ARAUCA/Arauca", "ARAUQUITA/Arauca", "CRAVO NORTE/Arauca", "FORTUL/Arauca", "PUERTO RONDON/Arauca", "SARAVENA/Arauca",
                "TAME/Arauca", "YOPAL/Casanare", "AGUAZUL/Casanare", "CHAMEZA/Casanare", "HATO COROZAL/Casanare", "LA SALINA/Casanare", "MANI/Casanare", "MONTERREY/Casanare",
                "NUNCHIA/Casanare", "OROCUE/Casanare", "PAZ DE ARIPORO/Casanare", "PORE/Casanare", "RECETOR/Casanare", "SABANALARGA/Casanare", "SACAMA/Casanare",
                "SAN LUIS DE PALENQUE/Casanare", "TAMARA/Casanare", "TAURAMENA/Casanare", "TRINIDAD/Casanare", "VILLANUEVA/Casanare", "MOCOA/Putumayo", "COLON/Putumayo",
                "ORITO/Putumayo", "PUERTO ASIS/Putumayo", "PUERTO CAICEDO/Putumayo", "PUERTO GUZMAN/Putumayo", "LEGUIZAMO/Putumayo", "SIBUNDOY/Putumayo",
                "SAN FRANCISCO/Putumayo", "SAN MIGUEL/Putumayo", "SANTIAGO/Putumayo", "VALLE DEL GUAMUEZ/Putumayo", "VILLAGARZON/Putumayo",
                "SAN ANDRES/Archipiélago de San Andrés, Providencia y Santa Catalina", "PROVIDENCIA/Archipiélago de San Andrés, Providencia y Santa Catalina",
                "LETICIA/Amazonas", "EL ENCANTO/Amazonas", "LA CHORRERA/Amazonas", "LA PEDRERA/Amazonas", "LA VICTORIA/Amazonas", "MIRITI - PARANA/Amazonas",
                "PUERTO ALEGRIA/Amazonas", "PUERTO ARICA/Amazonas", "PUERTO NARIÑO/Amazonas", "PUERTO SANTANDER/Amazonas", "TARAPACA/Amazonas", "INIRIDA/Guainía",
                "BARRANCO MINAS/Guainía", "MAPIRIPANA/Guainía", "SAN FELIPE/Guainía", "PUERTO COLOMBIA/Guainía", "LA GUADALUPE/Guainía", "CACAHUAL/Guainía",
                "PANA PANA/Guainía", "MORICHAL/Guainía", "SAN JOSE DEL GUAVIARE/Guaviare", "CALAMAR/Guaviare", "EL RETORNO/Guaviare", "MIRAFLORES/Guaviare",
                "MITU/Vaupés", "CARURU/Vaupés", "PACOA/Vaupés", "TARAIRA/Vaupés", "PAPUNAUA/Vaupés", "YAVARATE/Vaupés", "PUERTO CARREÑO/Vichada",
                "LA PRIMAVERA/Vichada", "SANTA ROSALIA/Vichada", "CUMARIBO/Vichada"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, municipios);
        municipio.setAdapter(adapter);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_perfil);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        UserName.setText(currentUser.getDisplayName());
        UserMail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPhoto);

        //Llena los campos del formulario con los datos de la bd
        consultarDatosPerfil(nombres);


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular)) {
                    registrar(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular);
                }
            }
        });
        return view;

    }


    private void registrar(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, Spinner municipio, EditText identificacion, EditText celular) {

        Map<String,Object> map= new HashMap<>();
        map.put("nombre",nombres.getText().toString());
        map.put("apellido",apellidos.getText().toString());
        map.put("municipio",municipio.getSelectedItem().toString());
        map.put("direccion",direccion.getText().toString());
        map.put("identificacion",identificacion.getText().toString());
        map.put("celular",celular.getText().toString());

        myRef.child(currentUser.getUid()).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nombres.setText("");
                        apellidos.setText("");
                        //municipio.setAdapter(null);
                        direccion.setText("");
                        identificacion.setText("");
                        celular.setText("");

                        Toast.makeText(getApplicationContext(), "Se actualizo de forma exitosa", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "No se actualizaron los datos, debido a un error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Consulta los datos del perfil del usuario logeado
    public void consultarDatosPerfil(EditText nombres){

    }

    public boolean validarCamposVacios(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, Spinner municipio, EditText identificacion, EditText celular){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String direccionV = direccion.getText().toString();
        String municipioV = municipio.getSelectedItem().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();

        if(nombreV.isEmpty()){
            nombres.setError("Debe diligenciar un nombre");
            campoLleno=false;
        }
        if(apellidoV.isEmpty()){
            apellidos.setError("Debe diligenciar un apellido");
            campoLleno=false;
        }
        if(direccionV.isEmpty()){
            direccion.setError("Debe diligenciar una dirección");
            campoLleno=false;
        }
        if(municipioV.equals("Municipio/Departamento")){
            Toast.makeText(getApplicationContext(), "Debe seleccionar un municipio", Toast.LENGTH_LONG).show();
            campoLleno=false;
        }
        if(identificacionV.isEmpty()){
            identificacion.setError("Debe diligenciar un nro. de identificación");
            campoLleno=false;
        }
        if(celularV.isEmpty()){
            celular.setError("Debe diligenciar un celular");
            campoLleno=false;
        }
        return campoLleno;

    }

}