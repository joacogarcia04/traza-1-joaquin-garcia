import Entidades.*;
import Repositorios.InMemoryRepository;

import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println(">>> INICIANDO PRUEBA DEL SISTEMA <<<");

        // Inicializar repositorio
        InMemoryRepository<Empresa> repoEmpresas = new InMemoryRepository<>();

        // Crear país y sucursales
        Pais chile = Pais.builder().nombre("Chile").build();

        Sucursal s1 = crearSucursal(10, "Sucursal Central", true, "Av. Libertad", 101, 1100, "Santiago", "Santiago", chile);
        Sucursal s2 = crearSucursal(20, "Sucursal Norte", false, "Calle Andes", 202, 2200, "Antofagasta", "Antofagasta", chile);
        Sucursal s3 = crearSucursal(30, "Sucursal Sur", true, "Ruta Sur", 303, 3300, "Puerto Montt", "Los Lagos", chile);
        Sucursal s4 = crearSucursal(40, "Sucursal Costa", false, "Boulevard Mar", 404, 4400, "Viña del Mar", "Valparaíso", chile);

        // Empresas
        Empresa e1 = Empresa.builder()
                .nombre("Compañía Alfa")
                .razonSocial("Alfa S.A.")
                .cuil(30444555666L)
                .sucursales(new HashSet<>(Set.of(s1, s2)))
                .build();

        Empresa e2 = Empresa.builder()
                .nombre("Compañía Beta")
                .razonSocial("Beta S.R.L.")
                .cuil(30999888777L)
                .sucursales(new HashSet<>(Set.of(s3, s4)))
                .build();

        // Vinculación bidireccional
        s1.setEmpresa(e1);
        s2.setEmpresa(e1);
        s3.setEmpresa(e2);
        s4.setEmpresa(e2);

        // Guardar en repositorio
        repoEmpresas.save(e1);
        repoEmpresas.save(e2);

        // Mostrar todas las empresas
        System.out.println(">>> EMPRESAS REGISTRADAS:");
        repoEmpresas.findAll().forEach(System.out::println);

        // Mostrar sucursales de empresa 2
        repoEmpresas.findById(2L).ifPresent(emp -> {
            System.out.println(">>> SUCURSALES DE " + emp.getNombre() + ":");
            for (Sucursal suc : emp.getSucursales()) {
                System.out.println(suc);
            }
        });

        // Buscar empresa por nombre
        System.out.println(">>> BUSCANDO COMPAÑÍA ALFA:");
        List<Empresa> filtradas = repoEmpresas.genericFindByField("nombre", "Compañía Alfa");
        for (Empresa emp : filtradas) {
            System.out.println(emp);
        }

        // Actualizar empresa 1
        Empresa e1Modificada = Empresa.builder()
                .id(1L)
                .nombre("Compañía Alfa Renovada")
                .razonSocial("Alfa Sociedad Actualizada")
                .cuil(30444555666L)
                .sucursales(e1.getSucursales())
                .build();

        repoEmpresas.genericUpdate(1L, e1Modificada);
        repoEmpresas.findById(1L).ifPresent(emp ->
                System.out.println(">>> EMPRESA ACTUALIZADA: " + emp)
        );

        // Eliminar empresa 1
        repoEmpresas.genericDelete(1L);
        if (repoEmpresas.findById(1L).isEmpty()) {
            System.out.println(">>> Empresa con ID 1 eliminada.");
        }

        // Mostrar empresas restantes
        System.out.println(">>> EMPRESAS RESTANTES:");
        for (Empresa emp : repoEmpresas.findAll()) {
            System.out.println(emp);
        }
    }

    // ---------------- MÉTODO AUXILIAR ----------------
    private static Sucursal crearSucursal(long id, String nombre, boolean matriz,
                                          String calle, int numero, int cp,
                                          String locNombre, String provNombre, Pais pais) {

        Provincia provincia = Provincia.builder()
                .id(id)
                .nombre(provNombre)
                .pais(pais)
                .build();

        Localidad localidad = Localidad.builder()
                .id(id)
                .nombre(locNombre)
                .provincia(provincia)
                .build();

        Domicilio dom = Domicilio.builder()
                .id(id)
                .calle(calle)
                .numero(numero)
                .cp(cp)
                .localidad(localidad)
                .build();

        return Sucursal.builder()
                .id(id)
                .nombre(nombre)
                .esCasaMatriz(matriz)
                .horarioApertura(LocalTime.of(8, 30))
                .horarioCierre(LocalTime.of(17, 30))
                .domicilio(dom)
                .build();
    }
}
