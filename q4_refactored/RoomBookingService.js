class BookingRequest {
    constructor(roomId, startDate, endDate, guest) {
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guest = guest;
    }
}

class Guest {
    constructor(name, phone) {
        this.name = name;
        this.phone = phone;
    }
}

class RoomBookingService {
    bookRoom(bookingRequest) {
        const { roomId, startDate, endDate, guest } = bookingRequest;
        console.log(`Booking room ${roomId} for ${guest.name} from ${startDate} to ${endDate}`);
    }
}
