# arteco-mvc

# TODO

* Path a nivel de clase
* Validaciones JSR 303
* Respuesta json?
* Mapeo de parámetros con objetos (y validaciones)


# Instrucciones

Realizar el checkout del código

shell # mvn clean install

shell # cd front-sample

shell # mvn tomcat:run


Una vez arrancado ir a http://localhost:8080/front-sample/



# Ejemplo de uso


<pre>

@SuppressWarnings("unused")
public class IndexController {


    @RequestMethod("/")
    public String index() {
        return "index";
    }

    @RequestMethod("/aviso-legal")
    public String avisoLegal() {
        return "section/aviso-legal";
    }


    @BeforeMethod
    public void before(Model model, HttpServletRequest request) {
        System.out.println("Executing before method " + request.getRequestURI());
    }

}

</pre>

Los métodos deben ir anotados por @RequestMethod. Adicionalmente se pueden declarar interceptores con @BeforeMethod and
@AfterMethod.  

Se pueden declarar los siguientes argumentos en cualquier método público anotado con @RequestMethod que serán injectados 
por el filtro:

* @ReqVar -> obtención de variables vía parámetros. Ej: metodo(@ReqVar("id") Long id){...}. Se hacen conversiones básicas de tipos.
* @PathVar -> obtención de variables de url. Ej: @RequestMethod("/{id}") public String metodo(@PathVar("id") Long id){...}. Se hacen conversiones básicas de tipos.
* Model -> traspaso de objetos a la vista
* HttpServletRequest
* HttpServletResponse

