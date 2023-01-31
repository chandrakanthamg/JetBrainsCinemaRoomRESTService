package cinema;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CinemaController {

    static int maxRows = 9;
    static int maxCols = 9;

    static List<Seat> seatsList;

    static {
        seatsList = new ArrayList<>();
        for (int i = 1; i <= maxRows; i++)
            for (int j = 1; j <= maxCols; j++) {
                Seat seat = new Seat();
                seat.setRow(i);
                seat.setColumn(j);
                seat.setAvailable(true);
                seat.setPrice(i <= 4 ? 10 : 8);
                seatsList.add(seat);
            }
    }

    @GetMapping("seats")
    SeatsResponse seats() {

        SeatsResponse cinemaResponse = new SeatsResponse();
        cinemaResponse.setTotalColumns(maxCols);
        cinemaResponse.setTotalRows(maxRows);
        cinemaResponse.setAvailableSeats(seatsList);
        return cinemaResponse;
    }

    @PostMapping("purchase")
    PurchaseResponse purchase(@RequestBody Seat seat) throws Exception {
        Optional<Seat> booked = seatsList.stream().filter(s -> s.getRow() == seat.getRow() && s.getColumn() == seat.getColumn()).findFirst();
        if (booked.isPresent()) {
            Seat bookedSeat = booked.get();
            if (bookedSeat.isAvailable()) {
                bookedSeat.setAvailable(false);
                bookedSeat.setToken(UUID.randomUUID().toString());
                PurchaseResponse purchaseResponse = new PurchaseResponse();
                purchaseResponse.setTicket(bookedSeat);
                purchaseResponse.setToken(bookedSeat.getToken());
                return purchaseResponse;
            } else {
                throw new Exception("The ticket has been already purchased!");
            }
        }
        throw new Exception("The number of a row or a column is out of bounds!");
    }

    @PostMapping("return")
    ReturnResponse returnMethod(@RequestBody ReturnRequest returnRequest) throws Exception {
        Optional<Seat> booked = seatsList.stream().filter(s -> s.getToken() != null && returnRequest.getToken() != null && s.getToken().equals(returnRequest.getToken())).findFirst();
        if (booked.isPresent()) {
            Seat bookedSeat = booked.get();
            bookedSeat.setAvailable(true);
            bookedSeat.setToken(null);
            ReturnResponse returnResponse = new ReturnResponse();
            returnResponse.setReturnedTicket(bookedSeat);
            return returnResponse;
        }
        throw new Exception("Wrong token!");
    }

    @PostMapping("stats")
    StatsResponse stats(@RequestParam("password") Optional<String> passwordOpt) throws Exception {
        String password = passwordOpt.orElse("");
        if (password == null || !password.equals("super_secret")) {
            throw new RuntimeException("The password is wrong!");
        }
        StatsResponse statsResponse = new StatsResponse();

        seatsList.stream().forEach(t -> {
            if (!t.isAvailable()) {
                statsResponse.setNumberOfPurchasedTickets(statsResponse.getNumberOfPurchasedTickets() + 1);
                statsResponse.setCurrentIncome(statsResponse.getCurrentIncome() + t.getPrice());
            } else {
                statsResponse.setNumberOfAvailableSeats(statsResponse.getNumberOfAvailableSeats() + 1);
            }
        });
        return statsResponse;
    }

}
