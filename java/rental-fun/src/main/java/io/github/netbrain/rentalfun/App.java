package io.github.netbrain.rentalfun;

import fi.iki.elonen.NanoHTTPD;
import io.github.netbrain.nicrofw.RouteNotFoundException;
import io.github.netbrain.nicrofw.Router;
import io.github.netbrain.nicrofw.handler.DefaultTypeConverter;
import io.github.netbrain.nicrofw.handler.TypeConverter;
import io.github.netbrain.rentalfun.core.bootstrap.Singletons;
import io.github.netbrain.rentalfun.customer.Customer;
import io.github.netbrain.rentalfun.customer.CustomerRepository;
import io.github.netbrain.rentalfun.film.Film;
import io.github.netbrain.rentalfun.film.FilmRepository;
import io.github.netbrain.rentalfun.rental.Rental;
import io.github.netbrain.rentalfun.rental.RentalRepository;
import org.boon.json.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

public class App extends NanoHTTPD {

    private final static Logger log = Logger.getLogger(App.class.getName());

    /** HTTP port **/
    public static final int PORT = 8080;
    /** Socket connections timeout **/
    public static final int TIMEOUT = 0;
    /** Resource repository **/
    private final Singletons singletons = Singletons.getInstance();
    /** Default type converter **/
    private final TypeConverter typeConverter = new DefaultTypeConverter();
    /** Router **/
    private final Router router;

    public static void main(String[] args) throws IOException {
        App app = new App();
        app.initSampleData(); //Remove this line to start with an empty database/repository
    }

    public App() throws IOException {
        super(PORT);

        router = new Router(singletons, typeConverter);
        router.scanAnnotations(getClass().getPackage().getName());

        System.out.println("\nRunning! http://localhost:" + PORT + "/ \n");

        start(TIMEOUT,false);
    }


    private void initSampleData(){
        CustomerRepository customerRepository = singletons.get(CustomerRepository.class);
        FilmRepository filmRepository = singletons.get(FilmRepository.class);
        RentalRepository rentalRepository = singletons.get(RentalRepository.class);

        for (Customer sampleEntity : new Customer[]{
                new Customer("john.smith"),
                new Customer("greggy2000"),
                new Customer("sarah_et76"),
        }) customerRepository.insert(sampleEntity);

        for (Film sampleEntity : new Film[]{
                new Film("Matrix 11", Film.Type.NEW),
                new Film("Spider Man", Film.Type.REGULAR),
                new Film("Spider Man 2", Film.Type.REGULAR),
                new Film("Out of Africe", Film.Type.OLD),
        }) filmRepository.insert(sampleEntity);

        Rental rental = new Rental(
                customerRepository.getById(0),
                3,
                filmRepository.getByIds(0, 1)
        );
        //5 days ago
        rental.setCreated(Date.from(
                LocalDate.now()
                        .minusDays(5)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ));
        rentalRepository.insert(rental);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Method method = session.getMethod();
            String uri = session.getUri();
            log.info(String.format("[%s] %s\n", method, uri));
            Object result = router.handle(session);
            if (result == null) {
                return newFixedLengthResponse(Response.Status.NO_CONTENT, "application/json", null);
            }
            ObjectMapper json = singletons.get(ObjectMapper.class);
            return newFixedLengthResponse(Response.Status.OK, "application/json", json.writeValueAsString(result));
        }catch (RouteNotFoundException e){
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", String.format("%s\n\nAvailable routes are:\n%s",e.getMessage(),e.getRoutes()));
        }catch (Exception e){
            StringWriter strWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(strWriter));
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", strWriter.toString());
        }
    }

}