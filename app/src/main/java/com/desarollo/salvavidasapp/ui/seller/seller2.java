package com.desarollo.salvavidasapp.ui.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import id.zelory.compressor.Compressor;

public class seller2 extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores, myRefPerfilUsuario, imgRef;
    Map<String,Object> selectedItems = new HashMap<>();

    Vendedores v;
    Session session;
    StorageReference storageReference;
    Bitmap thumb_bitmap = null;
    byte [] thumb_byte = null;
    ProgressDialog cargando;

    private String name;
    private String emailUser;
    private String correo="";
    private String contrasena="";
    ImageView foto_tienda=null;
    String urlFoto="";
    boolean esVendedor=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller2);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefPerfilUsuario = database.getReference("usuarios");
        imgRef = myRefVendedores.child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("vendedores");
        cargando = new ProgressDialog(this);

        TextView nombres = findViewById(R.id.tv_nombre);
        TextView apellidos = findViewById(R.id.tv_apellido);
        TextView identificacion = findViewById(R.id.tv_identidad);
        TextView celular = findViewById(R.id.tv_celular);
        EditText nombreEstablecimiento = findViewById(R.id.et_razon_social);
        EditText nit = findViewById(R.id.et_nit);
        Button btn_reg = findViewById(R.id.btn_registrar_vendedor);
        Button btn_ag_direccion = findViewById(R.id.agregar_direccion);
        TextView estado = findViewById(R.id.estado);
        Spinner sp_actividad_econimica = findViewById(R.id.sp_actividad_economica);
        TextView seleccionarFoto = findViewById(R.id.tv_seleccionar_foto);
        foto_tienda = findViewById(R.id.img_foto_tienda);
        EditText direccionVendedor = findViewById(R.id.et_direccion);

        String[] ArrayActividadesEconimicas = new String[]{
                "✚ Seleccione una actividad económica", "No Actualizada","No Registra","Asalariados","Otras actividades de servicio de apoyo a las empresas n.c.p.","Otras actividades profesionales, científicas y técnicas n.c.p.","Transporte de carga por carretera","Actividades de apoyo terapéutico","Rentistas de capital, solo personas naturales","Comercio al por menor en establecimientos no especializados con surtido compuesto principalmente por alimentos, bebidas o tabaco","Construcción de edificios residenciales","Otras actividades de servicios personales n.c.p.","Actividades de otras asociaciones n.c.p.","Transporte de pasajeros","Terminación y acabado de edificios y obras de ingeniería civil","Actividades de arquitectura e ingeniería y otras actividades conexas de consultoría técnica","Actividades de contabilidad, teneduría de libros, auditoría financiera y asesoría tributaria","Actividades inmobiliarias realizadas con bienes propios o arrendados","Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados.","Actividades jurídicas","Personas naturales sin actividad económica","Peluquería y otros tratamientos de belleza","Expendio a la mesa de comidas preparadas","Actividades combinadas de servicios administrativos de oficina","Actividades de la práctica médica, sin internación","Actividades de consultaría de gestión","Construcción de otras obras de ingeniería civil","Comercio al por menor de prendas de vestir y sus accesorios (incluye artículos de piel) en establecimientos especializados","Comercio al por menor en establecimientos no especializados, con surtido compuesto principalmente por productos diferentes de alimentos (víveres en general), bebidas y tabaco","Instalaciones eléctricas","Confección de prendas de vestir, excepto prendas de piel","Actividades de administración empresarial","Actividades de apoyo a la agricultura","Cría de ganado bovino y bufalino","Publicidad","Expendio de bebidas alcohólicas para el consumo dentro del establecimiento","Estudios de mercado y realización de encuestas de opinión pública","Personas naturales subsidiadas por terceros","Mantenimiento y reparación de vehículos automotores","Comercio al por menor de artículos de ferretería, pinturas y productos de vidrio en establecimientos especializados","Actividades de juegos de azar y apuestas","Comercio al por menor de productos farmacéuticos y medicinales, cosméticos y artículos de tocador en establecimientos especializados","Comercio al por menor de carnes (incluye aves de corral), productos cárnicos, pescados y productos de mar, en establecimientos especializados","Expendio de comidas preparadas en cafeterías","Comercio al por menor de productos agrícolas para el consumo en establecimientos especializados","Actividades de seguridad privada","Actividades de la práctica odontológica","Comercio de partes, piezas (autopartes) y accesorios (lujos) para vehículos automotores","Cultivo de café","Explotación mixta (agrícola y pecuaria)","Limpieza general interior de edificios","Investigaciones y desarrollo experimental en el campo de las ciencias sociales y las humanidades","Comercio al por menor de libros, periódicos, materiales y artículos de papelería y escritorio, en establecimientos especializados","Actividades de los hogares individuales como empleadores de personal doméstico","Educación de la primera infancia","Extracción de oro y otros metales preciosos","Educación básica primaria","Mantenimiento y reparación especializado de maquinaria y equipo","Otras actividades de atención de la salud humana","Actividades de mensajería","Otras actividades de telecomunicaciones","Actividades inmobiliarias realizadas a cambio de una retribución o por contrata","Actividades especializadas de diseño","Comercio al por menor de todo tipo de calzado y artículos de cuero y sucedáneos del cuero en establecimientos especializados.","Otros tipos de educación n.c.p.","Actividades de asociaciones religiosas","Otros tipos de expendio de comidas preparadas n.c.p.","Otras actividades de asistencia social sin alojamiento","Educación preescolar","Enseñanza deportiva y recreativa","Comercio al por menor de otros productos alimenticios n.c.p., en establecimientos especializados","Actividades de desarrollo de sistemas informáticos (planificación, análisis, diseño, programación, pruebas)","Recuperación de materiales","Actividades de centros de llamadas (Call center)","Actividades de apoyo a la educación","Comercio al por menor de bebidas y productos del tabaco, en establecimientos especializados","Educación básica secundaria","Comercio al por menor de computadores, equipos periféricos, programas de informática y equipos de telecomunicaciones en establecimientos especializados","Instalaciones de fontanería, calefacción y aire acondicionado","Comercio al por menor realizado a través de casas de venta o por correo","Elaboración de comidas y platos preparados","Otras actividades especializadas para la construcción de edificios y obras de ingeniería civil","Comercio al por menor de otros productos nuevos en establecimientos especializados","Elaboración de productos de panadería","Mantenimiento y reparación de computadores y de equipo periférico","Comercio al por menor de otros artículos domésticos en establecimientos especializados","Fabricación de productos metálicos para uso estructural","Manipulación de carga","Alquiler y arrendamiento de otros tipos de maquinaria, equipo y bienes tangibles n.c.p.","Construcción de edificios no residenciales","Actividades de agentes y corredores de seguros","Actividades de apoyo diagnóstico","Fotocopiado, preparación de documentos y otras actividades especializadas de apoyo a oficina","Actividades de consultoría informática y actividades de administración de instalaciones informáticas","Alquiler y arrendamiento de vehículos automotores","Otras actividades deportivas","Fabricación de partes y piezas de madera, de carpintería y ebanistería para la construcción","Actividades de espectáculos musicales en vivo","Educación de universidades","Investigaciones y desarrollo experimental en el campo de las ciencias naturales y la ingeniería","Actividades veterinarias","Comercio al por mayor de materias primas agropecuarias, animales vivos","Cultivo de arroz","Fabricación de muebles","Comercio al por mayor de materiales de construcción, artículos de ferretería, pinturas, productos de vidrio, equipo y materiales de fontanería y calefacción","Comercio al por menor de electrodomésticos y gasodomésticos de uso doméstico, muebles y equipos de iluminación","Comercio al por menor de leche, productos lácteos y huevos, en establecimientos especializados","Comercio al por mayor de productos alimenticios","Formación académica no formal","Otras actividades relacionadas con el mercado de valores","Otras actividades recreativas y de esparcimiento n.c.p.","Actividades de fotografía","Creación musical","Actividades combinadas de apoyo a instalaciones","Acabado de productos textiles","Comercio al por mayor no especializado","Otras actividades de tecnologías de información y actividades de servicios informáticos","Cultivo de hortalizas, raíces y tubérculos","Elaboración de otros productos alimenticios n.c.p.","Alojamiento en hoteles","Otras actividades de servicio de información n.c.p.","Construcción de carreteras y vías de ferrocarril","Fabricación de calzado de cuero y piel, con cualquier tipo de suela","Comercio al por mayor a cambio de una retribución o por contrata","Cultivo de frutas tropicales y subtropicales","Cultivo de cereales (excepto arroz), legumbres y semillas oleaginosas","Organización de convenciones y eventos comerciales","Actividades de apoyo a la ganadería","Actividades teatrales","Actividades de impresión","Cultivo de plátano y banano","Enseñanza cultural","Cría de aves de corral","Comercio al por menor de productos textiles en establecimientos especializados","Mantenimiento y reparación especializado de productos elaborados en metal","Otros cultivos transitorios n.c.p.","Otras actividades de limpieza de edificios e instalaciones industriales","Actividades de servicios relacionados con la impresión","Educación media técnica y de formación laboral","Actividades de paisajismo y servicios de mantenimiento conexos","Actividades de producción de películas cinematográficas, videos, programas, anuncios y comerciales de televisión","Establecimientos que combinan diferentes niveles de educación","Comercio de motocicletas y de sus partes, piezas y accesorios","Expendio por autoservicio de comidas preparadas","Aserrado, acepillado e impregnación de la madera","Comercio al por mayor de otros tipos de maquinaria y equipo n.c.p.","Catering para eventos","Lavado y limpieza, incluso la limpieza en seco, de productos textiles y de piel","Actividades de estaciones, vías y servicios complementarios para el transporte terrestre","Artes plásticas y visuales","Mantenimiento y reparación de otros efectos personales y enseres domésticos","Comercio al por menor de artículos y utensilios de uso doméstico","Mantenimiento y reparación de motocicletas y de sus partes y piezas","Servicio por horas","Procesamiento de datos, alojamiento (hosting) y actividades relacionadas","Otras industrias manufactureras n.c.p.","Transporte mixto","Actividades de las agencias de viaje","Actividades ejecutivas de la administración pública","Actividades de otros servicios de comidas","Actividades de hospitales y clínicas, con internación","Otras actividades complementarias al transporte","Comercio al por mayor de prendas de vestir","Actividades de telecomunicaciones alámbricas","Preparación del terreno","Actividades de bibliotecas y archivos","Comercio al por menor de combustible para automotores","Tratamiento y revestimiento de metales, mecanizado","Construcción de proyectos de servicio público","Comercio al por menor de artículos de segunda mano","Procesamiento y conservación de frutas, legumbres, hortalizas y tubérculos","Comercio al por mayor de productos farmacéuticos, medicinales, cosméticos y de tocador","Actividades de telecomunicaciones inalámbricas","Comercio al por mayor de bebidas y tabaco","Otras instalaciones especializadas","Fabricación de joyas, bisutería y artículos conexos","Educación de instituciones universitarias o de escuelas tecnológicas","Fabricación de otros tipos de calzado, excepto calzado de cuero y piel","Comercio al por mayor de desperdicios, desechos y chatarra","Cultivo de palma para aceite (palma africana) y otros frutos oleaginosos","Comercio al por menor de otros productos en puestos de venta móviles","Cría de ganado porcino","Comercio al por menor de lubricantes (aceites, grasas), aditivos y productos de limpieza para vehículos automotores","Comercio al por mayor de productos químicos básicos, cauchos y plásticos en formas primarias y productos químicos de uso agropecuario","Confección de artículos con materiales textiles, excepto prendas de vestir","Elaboración de productos lácteos","Reparación de muebles y accesorios para el hogar","Educación técnica profesional","Educación media académica","Comercio al por menor realizado a través de Internet","Captación, tratamiento y distribución de agua","Comercio al por menor de alimentos, bebidas y tabaco, en puestos de venta móviles","Actividades de envase y empaque","Actividades de servicios de sistemas de seguridad","Comercio al por mayor de otros utensilios domésticos n.c.p.","Fabricación de otros productos elaborados de metal n.c.p.","Regulación de las actividades de organismos que prestan servicios de salud, educativos, culturales y otros servicios sociales, excepto servicios de seguridad social","Comercio de vehículos automotores usados","Cultivo de caña de azúcar","Actividades de operadores turísticos","Creación audiovisual","Mantenimiento y reparación de aparatos y equipos domésticos y de jardinería","Pesca marítima","Mantenimiento y reparación especializado de equipo eléctrico","Otros servicios de reserva y actividades relacionadas","Fabricación de otros productos de madera, fabricación de artículos de corcho, cestería y espartería","Fabricación de jabones y detergentes, preparados para limpiar y pulir, perfumes y preparados de tocador","Extracción de piedra, arena, arcillas comunes, yeso y anhidrita","Alquiler y arrendamiento de otros efectos personales y enseres domésticos n.c.p.","Actividades de clubes deportivos","Comercio al por mayor de otros productos n.c.p.","Otras actividades de servicio financiero, excepto las de seguros y pensiones n.c.p.","Elaboración de productos de molinería","Comercio al por mayor de productos textiles, productos confeccionados para uso doméstico","Recolección de desechos no peligrosos","Otras actividades auxiliares de las actividades de servicios financieros n.c.p.","Actividades de programación y transmisión en el servicio de radiodifusión sonora","Actividades de las corporaciones financieras","Creación teatral","Extracción de hulla (carbón de piedra)","Cultivo de flor de corte","Actividades de saneamiento ambiental y otros servicios de gestión de desechos","Orden público y actividades de seguridad","Fabricación de artículos de plástico n.c.p.","Gestión de instalaciones deportivas","Actividades financieras de fondos de empleados y otras formas asociativas del sector solidario","Mantenimiento y reparación de aparatos electrónicos de consumo","Mantenimiento y reparación especializado de equipo electrónico y óptico","Pompas fúnebres y actividades relacionadas","Comercio de vehículos automotores nuevos","Seguros generales","Mantenimiento y reparación de equipos de comunicación","Actividades postales nacionales","Servicios de apoyo a la silvicultura","Comercio al por menor de artículos deportivos, en establecimientos especializados","Creación literaria","Propagación de plantas (actividades de los viveros, excepto viveros forestales)","Otras actividades de espectáculos en vivo","Fabricación de artículos de viaje, bolsos de mano y artículos similares elaborados en cuero, y fabricación de artículos de talabartería y guarnicionería","Comercio al por mayor de combustibles sólidos, líquidos, gaseosos y productos conexos","Comercio al por mayor de equipo, partes y piezas electrónicos y de telecomunicaciones","Extracción de madera","Fabricación de otros artículos textiles n.c.p.","Silvicultura y otras actividades forestales","Comercio al por mayor de computadores, equipo periférico y programas de informática","Comercio al por menor de productos textiles, prendas de vestir y calzado, en puestos de venta móviles","Educación tecnológica","Comercio al por menor de otros artículos culturales y de entretenimiento n.c.p. en establecimientos especializados","Fabricación de materiales de arcilla para la construcción","Otros cultivos permanentes n.c.p.","Otras actividades de suministro de recurso humano","Edición de periódicos, revistas y otras publicaciones periódicas","Mantenimiento y reparación de otros tipos de equipos y sus componentes n.c.p.","Comercio al por mayor de metales y productos metalíferos","Ensayos y análisis técnicos","Actividades de asociaciones empresariales y de empleadores","Actividades de detectives e investigadores privados","Procesamiento y conservación de carne y productos cárnicos","Actividades de grabación de sonido y edición de musica","Actividades de asistencia social sin alojamiento para personas mayores y discapacitadas","Edición de programas de informática (software)","Actividades de apoyo para otras actividades de explotación de minas y canteras","Actividades de sindicatos de empleados","Cultivo de plantas con las que se preparan bebidas","Actividades de programación y transmisión de televisión","Actividades de apoyo para la extracción de petróleo y de gas natural","Acuicultura de agua dulce","Fabricación de artículos de viaje, bolsos de mano y artículos similares, artículos de talabartería y guarnicionería elaborados en otros materiales","Corretaje de valores y de contratos de productos básicos","Otros tipos de alojamientos para visitantes","Comercio al por mayor de aparatos y equipo de uso doméstico","Comercio al por mayor de calzado","Fabricación de artículos de hormigón, cemento y yeso","Elaboración de cacao, chocolate y productos de confitería","Fabricación de instrumentos, aparatos y materiales médicos y odontológicos (incluido mobiliario)","Industrias básicas de hierro y de acero","Pesca de agua dulce","Cría de otros animales n.c.p.","Curtido y recurtido de cueros, recurtido y teñido de pieles","Actividades de asociaciones profesionales","Mantenimiento y reparación especializado de equipo de transporte, excepto los vehículos automotores, motocicletas y bicicletas","Transporte fluvial de pasajeros","Almacenamiento y depósito","Otras actividades de atención en instituciones con alojamiento","Actividades de los profesionales de compra y venta de divisas","Edición de libros","Elaboración de bebidas no alcohólicas, producción de aguas minerales y de otras aguas embotelladas","Tejeduría de productos textiles","Fabricación de pinturas, barnices y revestimientos similares, tintas para impresión y masillas","Actividades de agencias de cobranza y oficinas de calificación crediticia","Reparación de calzado y artículos de cuero","Actividades de agencias de noticias","Actividades posteriores a la cosecha","Fabricación de partes, piezas (autopartes) y accesorios (lujos) para vehículos automotores","Actividades de las compañías de financiamiento","Instalación especializada de maquinaria y equipo industrial","Actividades no diferenciadas de los hogares individuales como productores de servicios para uso propio","Otras actividades de distribución de fondos","Forja, prensado, estampado y laminado de metal, pulvimetalurgia","Fabricación de formas básicas de plástico","Actividades de jardines botánicos, zoológicos y reservas naturales","Comercio al por menor de equipos y aparatos de sonido y de video, en establecimientos especializados","Cultivo de plantas textiles","Actividades de planes de seguridad social de afiliación obligatoria","Elaboración de panela","Fabricación de motores, turbinas, y partes para motores de combustión interna","Comercio al por mayor de maquinaria y equipo agropecuarios","Fabricación de otros tipos de maquinaria y equipo de uso general n.c.p.","Actividades de atención en instituciones para el cuidado de personas mayores y/o discapacitadas","Fundición de hierro y de acero","Portales web","Actividades legislativas de la administración pública","Fabricación de tejidos de punto y ganchillo","Fabricación de otros artículos de papel y cartón","Fabricación de carrocerías para vehículos automotores, fabricación de remolques y semirremolques","Fabricación de otros productos químicos n.c.p.","Fabricación de formas básicas de caucho y otros productos de caucho n.c.p.","Actividades de telecomunicación satelital","Alojamiento rural","Fabricación de plásticos en formas primarias","Fabricación de productos farmacéuticos, sustancias químicas medicinales y productos botánicos de uso farmacéutico","Otros tipos de alojamiento n.c.p.","Fabricación de colchones y somieres","Fabricación de hojas de madera para enchapado, fabricación de tableros contrachapados, tableros laminados, tableros de partículas y otros tableros y paneles","Actividades de posproducción de películas cinematográficas, videos, programas, anuncios y comerciales de televisión","Transporte de pasajeros marítimo y de cabotaje","Fabricación de vidrio y productos de vidrio","Industrias básicas de otros metales no ferrosos","Otros trabajos de edición","Alojamiento en apartahoteles","Demolición","Actividades de agencias de empleo temporal","Evacuación y tratamiento de aguas residuales","Corte, tallado y acabado de la piedra","Fabricación de partes del calzado","Fabricación de juegos, juguetes y rompecabezas","Administración de justicia","Transporte fluvial de carga","Actividades de atención residencial medicalizada de tipo general","Fabricación de papel y cartón ondulado (corrugado), fabricación de envases, empaques y de embalajes de papel y cartón.","Generación de energía eléctrica","Alquiler y arrendamiento de equipo recreativo y deportivo","Fabricación de artículos de cuchillería, herramientas de mano y artículos de ferretería","Comercio al por menor de tapices, alfombras y cubrimientos para paredes y pisos en establecimientos especializados","Elaboración de alimentos preparados para animales","Fabricación de otros productos de cerámica y porcelana","Cultivo de especias y de plantas aromáticas y medicinales","Régimen de prima media con prestación definida (RPM)","Fabricación de sustancias y productos químicos básicos","Reencauche de llantas usadas","Fundición de metales no ferrosos","Actividades de defensa","Procesamiento y conservación de pescados, crustáceos y moluscos","Producción de gas, distribución de combustibles gaseosos por tuberías","Fabricación de productos refractarios","Actividades de organizaciones y entidades extraterritoriales","Actividades de agencias de empleo","Transporte de carga marítimo y de cabotaje","Descafeinado, tostión y molienda del café","Actividades no diferenciadas de los hogares individuales como productores de bienes para uso propio","Seguros de vida","Fabricación de otros tipos de maquinaria y equipo de uso especial n.c.p.","Fabricación de artículos y equipo para la práctica del deporte","Cultivo de tabaco","Fabricación de productos de la refinación del petróleo","Evaluación de riesgos y daños, y otras actividades de servicios auxiliares","Preparación e hilatura de fibras textiles","Transporte aéreo nacional de pasajeros","Industrias básicas de metales preciosos","Fabricación de artículos de piel","Actividades de exhibición de películas cinematográficas y videos","Fabricación de tapetes y alfombras para pisos","Actividades de aeropuertos, servicios de navegación aérea y demás actividades conexas al transporte aéreo","Fabricación de otros tipos de equipo eléctrico n.c.p.","Fabricación de artículos de punto y ganchillo","Elaboración de productos de tabaco","Transporte aéreo nacional de carga","Fideicomisos, fondos y entidades financieras similares","Actividades reguladoras y facilitadoras de la actividad económica","Actividades de puertos y servicios complementarios para el transporte acuático","Fabricación de abonos y compuestos inorgánicos nitrogenados","Extracción de esmeraldas, piedras preciosas y semipreciosas","Fabricación de tanques, depósitos y recipientes de metal, excepto los utilizados para el envase o transporte de mercancías","Fabricación de recipientes de madera","Tratamiento y disposición de desechos no peligrosos","Fabricación de llantas y neumáticos de caucho","Suministro de vapor y aire acondicionado","Administración de mercados financieros","Alojamiento en centros vacacionales","Cría de ovejas y cabras","Servicios de seguros sociales de salud","Actividades y funcionamiento de museos, conservación de edificios y sitios históricos","Actividades de compra de cartera o factoring","Elaboración de aceites y grasas de origen vegetal y animal","Actividades de las cooperativas financieras","Bancos comerciales","Construcción de barcos y de estructuras flotantes","Fabricación de plaguicidas y otros productos químicos de uso agropecuario","Extracción de petróleo crudo","Producción de copias a partir de grabaciones originales","Cría de caballos y otros equinos","Fabricación de fibras sintéticas y artificiales","Actividades de parques de atracciones y parques temáticos","Servicios de seguros sociales de riesgos profesionales","Fabricación de aparatos de uso doméstico","Alquiler de videos y discos","Actividades de asociaciones políticas","Capitalización","Fabricación de equipos eléctricos de iluminación","Fabricación de componentes y tableros electrónicos","Transporte férreo de pasajeros","Fabricación de cemento, cal y yeso","Extracción de arcillas de uso industrial, caliza, caolín y bentonitas","Acuicultura marítima","Fabricación de instrumentos musicales","Fabricación de otros productos minerales no metálicos n.c.p.","Actividades de los otros órganos de control","Actividades de las casas de cambio","Elaboración de almidones y productos derivados del almidón","Actividades de atención residencial, para el cuidado de pacientes con retardo mental, enfermedad mental y consumo de sustancias psicoactivas","Fabricación de maquinaria para la elaboración de alimentos, bebidas y tabaco","Transporte férreo de carga","Actividades de administración de fondos","Fabricación de maquinaria agropecuaria y forestal","Fabricación de máquinas formadoras de metal y de máquinas herramienta","Relaciones exteriores","Trilla de café","Arrendamiento de propiedad intelectual y productos similares, excepto obras protegidas por derechos de autor","Elaboración de bebidas fermentadas no destiladas","Construcción de embarcaciones de recreo y deporte","Fabricación de aparatos de distribución y control de la energía eléctrica","Fabricación de bicicletas y de sillas de ruedas para personas con discapacidad","Transporte aéreo internacional de carga","Transporte por tuberías","Banca de segundo piso","Extracción de otros minerales no metálicos n.c.p.","Fabricación de equipo de medición, prueba, navegación y control","Recolección de desechos peligrosos","Banco Central","Fabricación de equipo de elevación y manipulación","Fabricación de productos de hornos de coque","Actividades de distribución de películas cinematográficas, videos, programas, anuncios y comerciales de televisión","Fabricación de vehículos automotores y sus motores","Fabricación de maquinaria para explotación de minas y canteras y para obras de construcción","Destilación, rectificación y mezcla de bebidas alcohólicas","Fabricación de equipos de potencia hidráulica y neumática","Producción de malta, elaboración de cervezas y otras bebidas malteadas","Otros derivados del café","Tratamiento y disposición de desechos peligrosos","Fabricación de hornos, hogares y quemadores industriales","Fabricación de cuerdas, cordeles, cables, bramantes y redes","Fabricación de aparatos electrónicos de consumo","Fabricación de motores, generadores y transformadores eléctricos","Extracción de otros minerales metalíferos no ferrosos n.c.p.","Recolección de productos forestales diferentes a la madera","Comercialización de energía eléctrica","Fabricación de instrumentos ópticos y equipo fotográfico","Actividades de zonas de camping y parques para vehículos recreacionales","Fabricación de pulpas (pastas) celulósicas, papel y cartón","Distribución de energía eléctrica","Fabricación de maquinaria para la elaboración de productos textiles, prendas de vestir y cueros","Extracción de carbón lignito","Tratamiento de semillas para propagación","Fabricación de otras bombas, compresores, grifos y válvulas","Elaboración de macarrones, fideos, alcuzcuz y productos farináceos similares","Fabricación de caucho sintético en formas primarias","Extracción de minerales para la fabricación de abonos y productos químicos","Fabricación de hilos y cables eléctricos y de fibra óptica","Fabricación de maquinaria para la metalurgia","Extracción de minerales de hierro","Reaseguros","Transporte aéreo internacional de pasajeros","Fabricación de equipo de irradiación y equipo electrónico de uso médico y terapéutico","Transmisión de energía eléctrica","Fabricación de pilas, baterías y acumuladores eléctricos","Fabricación de aeronaves, naves espaciales y de maquinaria conexa","Fabricación de cojinetes, engranajes, trenes de engranajes y piezas de transmisión","Fabricación de equipos de comunicación","Fabricación de generadores de vapor, excepto calderas de agua caliente para calefacción central","Fabricación de otros tipos de equipo de transporte n.c.p.","Régimen de ahorro individual (RAI)","Fabricación de maquinaria y equipo de oficina (excepto computadoras y equipo periférico)","Leasing financiero (arrendamiento financiero)","Fabricación de computadoras y de equipo periférico","Caza ordinaria y mediante trampas y actividades de servicios conexas","Elaboración y refinación de azúcar","Fabricación de dispositivos de cableado","Extracción de halita (sal)","Edición de directorios y listas de correo","Fabricación de motocicletas","Actividad de mezcla de combustibles","Fabricación de relojes","Fabricación de herramientas manuales con motor","Extracción de gas natural","Fondos de cesantías","Instituciones especiales oficiales","Extracción de minerales de uranio y de torio","Fabricación de locomotoras y de material rodante para ferrocarriles","Extracción de minerales de níquel","Fabricación de medios magnéticos y ópticos para almacenamiento de datos","Fabricación de armas y municiones"
        };

        ArrayList<String> listActividadesEconomicas = new ArrayList(Arrays.asList(ArrayActividadesEconimicas));
        ArrayAdapter<String> adapterActividadesEconomicas = new ArrayAdapter<String>(seller2.this, R.layout.spinner_item_modified, listActividadesEconomicas);
        sp_actividad_econimica.setAdapter(adapterActividadesEconomicas);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                name = profile.getDisplayName();
                //UserName.setText(name);
                emailUser = profile.getEmail();
                //UserMail.setText(email);
                /*
                Uri photoUrl = profile.getPhotoUrl();
                Glide.with(UserPhoto.getContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(UserPhoto);
                 */
            }
        }

        Intent intent = getIntent();
        if (intent.getExtras()  != null){
            Bundle extras = getIntent().getExtras();
            String direccion = extras.getString("direccion");
            direccionVendedor.setText(direccion);

        }
        consultarDatosVendedor(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica,
                btn_reg,estado,direccionVendedor);

        consultarDatosPerfilUsuario(nombres, apellidos, identificacion, celular);

        seleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(seller2.this);
            }
        });

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios( nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica,
                        urlFoto,direccionVendedor)) {
                    registrar(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica, estado,direccionVendedor);

                    FancyAlertDialog.Builder
                            .with(seller2.this)
                            .setTitle("Felicitaciones !")
                            .setBackgroundColor(Color.parseColor("#EC7063"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                            .setMessage("Se realizo el proceso de forma exitosa !")
                            .setPositiveBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                            .setPositiveBtnText("Ok")
                            .setNegativeBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                            .setNegativeBtnText("Volver")
                            .setAnimation(Animation.POP)
                            .isCancellable(true)
                            .setIcon(R.drawable.icono_ok, View.VISIBLE)
                            .onPositiveClicked(new FancyAlertDialogListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    Intent intent = new Intent(seller2.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }})
                            .onNegativeClicked(dialog -> Toast.makeText(seller2.this, "Cancel", Toast.LENGTH_SHORT).show())
                            .build()
                            .show();

                    if(!esVendedor){
                        enviar_email(correo,contrasena, nombres, celular);
                        enviar_email_usuario(correo,contrasena, nombres, celular);

                        FancyAlertDialog.Builder
                                .with(seller2.this)
                                .setTitle("Buen Trabajo!")
                                .setBackgroundColor(Color.parseColor("#EC7063"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                .setMessage("Se realizo el proceso de forma exitosa, ahora analizaremos tu solicitud!")
                                .setPositiveBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                .setPositiveBtnText("Ok")
                                .setNegativeBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                .setNegativeBtnText("Volver")
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.icono_ok, View.VISIBLE)
                                .onPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        Intent intent = new Intent(seller2.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                    }})
                                .onNegativeClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                    }})
                                .build()
                                .show();
                    }
                }
            }
        });

        btn_ag_direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gpsActivo = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (gpsActivo != false){
                    Intent h = new Intent(seller2.this, Maps.class);
                    h.putExtra("tipo", "vendedores");
                    startActivity(h);

                }
                else{
                    Toast.makeText(seller2.this,"activa el GPS para poder continuar...",Toast.LENGTH_SHORT).show();
                }*/

                crearModalDirecciones(direccionVendedor);
            }
        });

    }//fin OnCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(seller2.this,data);

            //recortar imagen
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(1000, 1000)
                    .setAspectRatio(1,1)
                    .start(seller2.this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                //Picasso.with(seller2.this).load(url).into(foto_tienda);

                Glide.with(seller2.this)
                        .load(url)
                        //.apply(RequestOptions.circleCropTransform())
                        .into(foto_tienda);
                comprimirImagen(url);
            }
            cargando.setTitle("Subiendo foto");
            cargando.setMessage("Cargando...");
            cargando.show();

            final StorageReference ref = storageReference.child(currentUser.getUid()).child("logo").child("logo_empresa");
            UploadTask uploadTask = ref.putBytes(thumb_byte);

            //subir imagen en Storage
            Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //Actualizar URL en la BD
                    Uri downloaduri = task.getResult();
                    imgRef.child("url_logo").setValue(downloaduri.toString());
                    urlFoto = downloaduri.toString();
                    cargando.dismiss();
                }
            });
        }
    }

    public void comprimirImagen(File url){
        //comprimiendo imagen
        try{
            thumb_bitmap = new Compressor(seller2.this)
                    .setMaxWidth(1000)
                    .setMaxHeight(1000)
                    .setQuality(90)
                    .compressToBitmap(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
        thumb_byte = byteArrayOutputStream.toByteArray();
        //fin del compresor
    }

    public void consultarDatosVendedor(TextView tv_nombres, TextView tv_apellidos, TextView et_identificacion,
                                       TextView tv_celular, EditText et_nombreEstablecimiento, EditText et_nit,
                                       Spinner sp_actividad_econimica, Button btn_reg, TextView tv_estado,EditText direccionVendedor){

        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if (snapshot.child("nombre").exists()) {
                                String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                                tv_nombres.setText(nombre);
                                btn_reg.setText("Actualizar");
                            }
                            if (snapshot.child("apellido").exists()) {
                                String apellido = Objects.requireNonNull(snapshot.child("apellido").getValue()).toString();
                                tv_apellidos.setText(apellido);
                            }
                            if (snapshot.child("identificacion").exists()) {
                                String identificacion = Objects.requireNonNull(snapshot.child("identificacion").getValue()).toString();
                                et_identificacion.setText(identificacion);
                            }
                            if (snapshot.child("celular").exists()) {
                                String celular = Objects.requireNonNull(snapshot.child("celular").getValue()).toString();
                                tv_celular.setText(celular);
                            }
                            if (snapshot.child("nombre_establecimiento").exists()) {
                                String nombre_establecimiento = Objects.requireNonNull(snapshot.child("nombre_establecimiento").getValue()).toString();
                                et_nombreEstablecimiento.setText(nombre_establecimiento);
                            }
                            if (snapshot.child("nit").exists()) {
                                String nit = Objects.requireNonNull(snapshot.child("nit").getValue()).toString();
                                et_nit.setText(nit);
                            }
                            if (snapshot.child("actividad_economica").exists()) {
                                String actividad_economica = Objects.requireNonNull(snapshot.child("actividad_economica").getValue()).toString();
                                String[] ArrayActividadEconomica = new String[]{actividad_economica, "✚ Seleccione una actividad económica", "Actividad 1", "Actividad 2", "Actividad 3", "Actividad 4"};
                                ArrayList<String> listActividadesEconomicas = new ArrayList(Arrays.asList(ArrayActividadEconomica));
                                ArrayAdapter<String> adapterActividadesEconomicas = new ArrayAdapter<String>(seller2.this, R.layout.spinner_item_modified, listActividadesEconomicas);
                                sp_actividad_econimica.setAdapter(adapterActividadesEconomicas);
                            }
                            if (snapshot.child("estado").exists()) {
                                String estado = Objects.requireNonNull(snapshot.child("estado").getValue()).toString();
                                tv_estado.setText(estado);
                                if (estado.equals("Aprobado") || estado.equals("Aceptado")){
                                    esVendedor=true;
                                }
                            }

                            if (snapshot.child("direccion").exists()) {
                                String direccion = Objects.requireNonNull(snapshot.child("direccion").getValue()).toString();
                                direccionVendedor.setText(direccion);
                            }

                            if (snapshot.child("url_logo").exists()) {
                                urlFoto = Objects.requireNonNull(snapshot.child("url_logo").getValue()).toString();
                                Glide.with(getApplicationContext())
                                        .load(urlFoto)
                                        //.apply(RequestOptions.circleCropTransform())
                                        .into(foto_tienda);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(seller2.this, "Error consultando los datos del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosPerfilUsuario(TextView tv_nombres, TextView tv_apellidos, TextView tv_identificacion,
                                            TextView tv_celular){
        myRefPerfilUsuario.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("nombre").exists()) {
                                String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                                tv_nombres.setText(nombre);
                            }
                            if(snapshot.child("apellido").exists()) {
                                String apellido = Objects.requireNonNull(snapshot.child("apellido").getValue()).toString();
                                tv_apellidos.setText(apellido);
                            }
                            if(snapshot.child("identificacion").exists()) {
                                String identificacion = Objects.requireNonNull(snapshot.child("identificacion").getValue()).toString();
                                tv_identificacion.setText(identificacion);
                            }
                            if(snapshot.child("celular").exists()) {
                                String celular = Objects.requireNonNull(snapshot.child("celular").getValue()).toString();
                                tv_celular.setText(celular);
                            }
                        }else{
                            Toast.makeText(seller2.this, "Debes completar primero el perfil del comprador", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(seller2.this, "Error consultando los datos del perfil. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrar(TextView nombres, TextView apellidos, TextView identificacion, TextView celular, EditText nombre_establecimiento, EditText nit, Spinner sp_actividad_econimica,
                           TextView estado, EditText direccionVendedor) {
        v = new Vendedores();
        v.setNombre(nombres.getText().toString());
        v.setApellido(apellidos.getText().toString());
        v.setIdentificacion(identificacion.getText().toString());
        v.setCelular(celular.getText().toString());
        if(estado.getText().toString().equals("SIN REGISTRO")){
            estado.setText("Pendiente");
        }
        v.setEstado(estado.getText().toString());
        v.setNombre_establecimiento(nombre_establecimiento.getText().toString());
        v.setNit(nit.getText().toString());
        v.setActividad_economica(sp_actividad_econimica.getSelectedItem().toString());
        v.setUrl_logo(urlFoto);
        v.setDireccion(direccionVendedor.getText().toString());

        //guarda los datos del vendedor
        /*
        myRefVendedores.child(currentUser.getUid()).setValue(v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "Solicitud registrada correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), "Error registrando la solicitud. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
         */

        myRefVendedores.child(currentUser.getUid()).child("actividad_economica").setValue(v.getActividad_economica())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("apellido").setValue(v.getApellido())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("celular").setValue(v.getCelular())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("direccion").setValue(v.getDireccion())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("estado").setValue(v.getEstado())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("identificacion").setValue(v.getIdentificacion())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("nit").setValue(v.getNit())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("nombre").setValue(v.getNombre())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("nombre_establecimiento").setValue(v.getNombre_establecimiento())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        myRefVendedores.child(currentUser.getUid()).child("url_logo").setValue(v.getUrl_logo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        //registrar_token();
    }

    public void enviar_email( String correo, String contrasena, TextView nombres, TextView celular){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola Administrador de Salvavidas App, <b>" + nombres.getText().toString() + "</b> desea ser vendedor en nuestra APP y"
                + " lo puedes contactar en el número móvil: <u>" + celular.getText().toString() + "</u></p><br>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
                "<p style='text-align: justify'><font size=1><i>Este mensaje y sus archivos adjuntos van dirigidos exclusivamente a su destinatario pudiendo contener información confidencial " +
                "sometida a secreto profesional. No está permitida su reproducción o distribución sin la autorización expresa de SALVAVIDAS APP, Si usted no es el destinatario " +
                "final por favor elimínelo e infórmenos por esta vía. Según la Ley Estatutaria 1581 de 2.012 de Protección de Datos y sus normas reglamentarias, " +
                "el Titular presta su consentimiento para que sus datos, facilitados voluntariamente, pasen a formar parte de una base de datos, cuyo responsable " +
                "es SALVAVIDAS APP, cuyas finalidades son: Gestión administrativa, Gestión de clientes, Prospección comercial, Fidelización de clientes, Marketing y " +
                "el envío de comunicaciones comerciales sobre nuestros productos y/o servicios. Puede usted ejercer los derechos de acceso, corrección, supresión, " +
                "revocación o reclamo por infracción sobre sus datos, mediante escrito dirigido a SALVAVIDAS APP a la dirección de correo electrónico " +
                "ceo@salvavidas.app indicando en el asunto el derecho que desea ejercer, o mediante correo ordinario remitido a la Carrera XX # XX – XX Medellín, Antioquia." +
                "</font></i></p>";
        //String to_ = to.getText().toString();
        String to_ = "ceo@salvavidas.app";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoEnvia,contrasenaCorreoEnvia);
                }
            });
            if(session!=null){
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correoEnvia));
                message.setSubject("Solicitud de nuevo vendedor Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Solicitud enviada correctamente", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void enviar_email_usuario( String correo, String contrasena, TextView nombres, TextView celular){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola <b>" + nombres.getText().toString() + "</b> recibimos con agrado tu solicitud "
                + "y te estaremos respondiendo en el menor tiempo posible.<br></p><br>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
                "<p style='text-align: justify'><font size=1><i>Este mensaje y sus archivos adjuntos van dirigidos exclusivamente a su destinatario pudiendo contener información confidencial " +
                "sometida a secreto profesional. No está permitida su reproducción o distribución sin la autorización expresa de SALVAVIDAS APP, Si usted no es el destinatario " +
                "final por favor elimínelo e infórmenos por esta vía. Según la Ley Estatutaria 1581 de 2.012 de Protección de Datos y sus normas reglamentarias, " +
                "el Titular presta su consentimiento para que sus datos, facilitados voluntariamente, pasen a formar parte de una base de datos, cuyo responsable " +
                "es SALVAVIDAS APP, cuyas finalidades son: Gestión administrativa, Gestión de clientes, Prospección comercial, Fidelización de clientes, Marketing y " +
                "el envío de comunicaciones comerciales sobre nuestros productos y/o servicios. Puede usted ejercer los derechos de acceso, corrección, supresión, " +
                "revocación o reclamo por infracción sobre sus datos, mediante escrito dirigido a SALVAVIDAS APP a la dirección de correo electrónico " +
                "ceo@salvavidas.app indicando en el asunto el derecho que desea ejercer, o mediante correo ordinario remitido a la Carrera XX # XX – XX Medellín, Antioquia." +
                "</font></i></p>";
        //String to_ = to.getText().toString();
        String to_ = emailUser;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoEnvia,contrasenaCorreoEnvia);
                }
            });
            if(session!=null){
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correoEnvia));
                message.setSubject("Solicitud de nuevo vendedor Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                //Toast.makeText(getApplicationContext(), "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }
    public boolean validarCamposVacios(TextView nombres, TextView apellidos, TextView identificacion, TextView celular,
                                       EditText nombre_establec, EditText et_nit, Spinner sp_actividades_economicas, String urlFoto,EditText direccionVendedor){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();
        String nombre_establecimiento = nombre_establec.getText().toString();
        String nit = et_nit.getText().toString();
        String actividades_econo = sp_actividades_economicas.getSelectedItem().toString();
        String direccion = direccionVendedor.getText().toString();

        if(nombreV.equals("Nombres")){
            nombres.setError("Debe diligenciar un nombre");
            Toast.makeText(getApplicationContext(), "Debe completar primero el perfil de comprador", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        if(apellidoV.equals("Apellidos")){
            apellidos.setError("Debe diligenciar un apellido");
            campoLleno=false;
        }
        if(identificacionV.equals("Documento de identidad")){
            identificacion.setError("Debe diligenciar un nro. de identificación");
            campoLleno=false;
        }else if (identificacionV.length() < 5 || identificacionV.length() > 15){
            identificacion.setError("Digite un número de cédula válido");
            campoLleno=false;
        }
        if(celularV.equals("Celular")){
            celular.setError("Debe diligenciar un celular");
            campoLleno=false;
        }else if (celularV.length() != 10){
            celular.setError("El celular debe tener 10 digitos");
            campoLleno=false;
        }
        if(nombre_establecimiento.isEmpty()){
            nombre_establec.setError("Debe diligenciar un nombre de establecimiento");
            campoLleno=false;
        }
        if(direccion.isEmpty()){
            direccionVendedor.setError("Debe diligenciar la direccion");
            campoLleno=false;
        }
        if(direccion.contains("Dirección")){
            direccionVendedor.setError("Debe registrar una dirección válida");
            campoLleno=false;
        }
        if(nit.isEmpty()){
            et_nit.setError("Debe diligenciar un NIT");
            campoLleno=false;
        }if(actividades_econo.equals("✚ Seleccione una actividad económica")){
            Toast.makeText(getApplicationContext(), "Seleccione una actividad económica", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(urlFoto.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor cargue el logo de su empresa", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        return campoLleno;
    }


    private void crearModalDirecciones(EditText et_direccion_producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(seller2.this );
        builder.setTitle("Elige una dirección");
        builder.setCancelable(false);

        String[] direcciones = new String[]
                {
                        "Dirección 1", "Dirección 2", "Dirección 3", "Dirección 4", "Dirección 5"
                };

        //array booleano para marcar casillas por defecto
        boolean[] checkItems = new boolean[]{
                false, false, false, false, false
        };

        //consulta las direcciones del vendedor
        myRefPerfilUsuario.child(currentUser.getUid()).child("mis direcciones")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i=0;
                        if(snapshot.exists()){
                            for(DataSnapshot objsnapshot : snapshot.getChildren()){
                                ListDirecciones d = objsnapshot.getValue(ListDirecciones.class);
                                direcciones[i] = d.getDireccionUsuario();
                                checkItems[i]=false;
                                if (i < direcciones.length){
                                    i=i+1;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando las direcciones. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

        final List<String> foodList = Arrays.asList(direcciones);

        builder.setMultiChoiceItems(direcciones, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkItems[i] = b; //verificar si existe un item seleccionado
                String currentItems = foodList.get(i); //Obtener el valor seleccionado
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedItems.clear();
                //recorre los items y valida cuales fueron checkeados
                for(int x=0;x<checkItems.length;x++){
                    boolean checked = checkItems[x];
                    if(checked){
                        selectedItems.put(foodList.get(x),checked);
                        et_direccion_producto.setText(foodList.get(x));
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}