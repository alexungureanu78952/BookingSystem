# Enterprise Booking System

A Java-based distributed booking system with a desktop client and socket-based server backend, featuring real-time slot management, user authentication, and dynamic theming.

## 🎯 Implemented Features

### Authentication & User Management
- **User Registration & Login** - Secure username/password authentication
- **User Profiles** - View and edit user information (email, full name)
- **Token-Based Sessions** - Client token system for session management
- **Authentication Validation** - Server-side verification of all commands

### Booking Management
- **View Available Slots** - Real-time list of bookable time slots
- **Reserve Slots** - Book available slots with conflict prevention
- **My Bookings** - View all personal active bookings
- **Cancel Bookings** - Release booked slots back to availability
- **Automatic Slot Status** - Slots automatically become unavailable/available
- **Data Persistence** - Bookings linked to users for multi-session access

### User Interface
- **Dark/Light Theme** - Toggle between dark and light color schemes
- **Themed Components** - All UI elements respect theme settings
- **Centered Layouts** - Professional 520px-wide centered form fields
- **Tab-Based Navigation** - Separate tabs for slots and bookings
- **Modern Buttons** - Green-accented action buttons with hover effects
- **Responsive Design** - Adapts to window resizing

### Data Management
- **Request Tracking** - Prevents booking list from disappearing on empty responses
- **Immediate Load on Login** - Bookings and slots load when authenticated
- **Theme-Aware Forms** - All text fields update colors with theme changes

## 💻 Technologies Used

### Server
- **Framework**: Quarkus (Java)
- **Database**: Hibernate ORM + SQL (PostgreSQL/H2)
- **Architecture**: Microservice-ready
- **Communication**: Object serialization over TCP sockets
- **Concurrency**: Synchronized booking methods for thread safety

### Client
- **UI Framework**: Java Swing
- **Architecture**: MVC with CardLayout for view switching
- **Communication**: Object I/O streams with server
- **Threading**: Background threads for non-blocking operations

### Build & Dependencies
- **Build Tool**: Maven
- **Java Version**: JDK 21+
- **Dependencies**: Quarkus extensions (Hibernate, REST-easy if REST API added)

## 🏗️ Architecture

```
BookingSystem/
├── Client/
│   ├── src/main/java/
│   │   ├── booking/client/
│   │   │   ├── BookingClient.java (Socket communication)
│   │   │   ├── ClientApplication.java (Entry point)
│   │   │   └── ui/
│   │   │       ├── BookingGUI.java (Main window)
│   │   │       ├── AuthPanel.java (Login/Register)
│   │   │       ├── ThemeManager.java (Dark/Light theming)
│   │   │       ├── TimeSlotCard.java (Slot display)
│   │   │       ├── BookingCard.java (Booking display)
│   │   │       ├── ModernButton.java (Custom buttons)
│   │   │       └── UserProfilePanel.java (Profile management)
│   │   └── shareable/ (DTOs and commands)
│   └── pom.xml
│
└── Server/
    ├── src/main/java/server/
    │   ├── ServerApplication.java (Entry point)
    │   ├── entity/
    │   │   ├── User.java
    │   │   ├── TimeSlot.java
    │   │   └── Booking.java (with User relationship)
    │   ├── repository/ (Database access)
    │   │   ├── UserRepository.java
    │   │   ├── TimeSlotRepository.java
    │   │   └── BookingRepository.java
    │   ├── service/
    │   │   ├── AuthService.java (Login/Register logic)
    │   │   └── BookingService.java (Booking operations)
    │   └── socket/
    │       ├── SocketServer.java (Listener)
    │       └── ClientHandler.java (Request processing)
    └── pom.xml
```

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### Running the Server
```bash
cd Server
mvn quarkus:dev
# Server listens on localhost:9090
```

### Running the Client
```bash
cd Client
mvn exec:java@run
# or build and run compiled classes
```

## 📋 Key Workflows

### User Registration & Login
1. User registers with username, email, password, full name
2. Credentials stored with hashed passwords
3. Login verifies credentials and returns UserDTO
4. Client stores user info and switches to main view
5. Bookings automatically load after authentication

### Booking a Slot
1. User views available slots from server
2. Selects a slot and clicks "Book Slot"
3. Server validates slot availability
4. Creates booking linked to user
5. Marks slot as unavailable
6. Updates My Bookings tab

### Theme Toggle
1. User clicks theme menu item
2. ThemeManager toggles dark/light
3. All components registered with ThemeManager receive notification
4. Colors update in real-time (backgrounds, text, borders)
5. Theme preference persists until app restart

## 🔮 Future Enhancements

### Short Term
- [ ] Persistent theme preference (save to config file)
- [ ] Booking confirmation dialogs
- [ ] Time zone support for international deployments
- [ ] Bulk slot import/export
- [ ] Detailed slot descriptions and location info

### Medium Term
- [ ] REST API alongside socket communication
- [ ] Web client (React/Vue.js)
- [ ] Email notifications on booking confirmation/cancellation
- [ ] Advanced filtering and search for bookings/slots
- [ ] Booking history with past bookings archive

### Long Term
- [ ] Admin dashboard for slot management
- [ ] User roles (admin, organizer, participant)
- [ ] Multi-location/resource booking
- [ ] Recurring bookings
- [ ] Mobile app (React Native/Flutter)
- [ ] Analytics and reporting
- [ ] Calendar view integration
- [ ] Waitlist for fully booked slots
- [ ] Rating/feedback system for slots
- [ ] Integration with external calendars (Google Calendar, Outlook)

## 🐛 Known Issues & TODOs
- Server classes need Booking-User relationship migration (foreign key constraint)
- Booking reload on app restart requires fresh login (persistence needs improvement)
- Theme colors don't apply to scrollbar in some LnF
- StatusLabel removed from footer (was just noise)

## 📝 Configuration

### Server (application.properties)
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=booking_user
quarkus.datasource.password=secure_password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/booking_db
```

### Client
- Server host/port: localhost:9090 (hardcoded in BookingClient.java)
- Theme: Dark mode by default (editable in ThemeManager)

## 📄 License
Proprietary - Enterprise Booking System

## 👥 Contributors
Development team - Enterprise Solutions Division

---

**Last Updated**: January 2026  
**Version**: 1.0 Beta
