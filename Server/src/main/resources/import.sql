-- Date de test pentru dezvoltare
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES
(1, '2025-01-20 09:00:00', '2025-01-20 10:00:00', 'Morning Consultation', true, 0),
(2, '2025-01-20 10:00:00', '2025-01-20 11:00:00', 'Mid-Morning Session', true, 0),
(3, '2025-01-20 11:00:00', '2025-01-20 12:00:00', 'Late Morning Slot', true, 0),
(4, '2025-01-20 14:00:00', '2025-01-20 15:00:00', 'Afternoon Session', true, 0),
(5, '2025-01-20 15:00:00', '2025-01-20 16:00:00', 'Late Afternoon Slot', true, 0);

-- Secvențele vor fi actualizate automat
SELECT setval('time_slots_id_seq', (SELECT MAX(id) FROM time_slots));
SELECT setval('bookings_id_seq', 1);
