INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (1, '2025-01-20 09:00:00', '2025-01-20 10:00:00', 'Consultatie Dimineata', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (2, '2025-01-20 10:00:00', '2025-01-20 11:00:00', 'Sesiune Coaching', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (3, '2025-01-20 11:30:00', '2025-01-20 12:30:00', 'Interviu Tehninc', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (4, '2025-01-20 14:00:00', '2025-01-20 15:00:00', 'Meeting Proiect', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (5, '2025-01-20 15:30:00', '2025-01-20 16:30:00', 'Workshop Java', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (6, '2025-01-21 09:00:00', '2025-01-21 10:00:00', 'Consultatie (Marti)', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (7, '2025-01-21 11:00:00', '2025-01-21 12:00:00', 'Sesiune Feedback', true, 0) ON CONFLICT (id) DO NOTHING;
INSERT INTO time_slots (id, start_time, end_time, description, available, version) VALUES (8, '2025-01-22 10:00:00', '2025-01-22 11:00:00', 'Prezentare Demo', true, 0) ON CONFLICT (id) DO NOTHING;

SELECT setval('time_slots_id_seq', (SELECT MAX(id) FROM time_slots));
SELECT setval('bookings_id_seq', COALESCE((SELECT MAX(id) FROM bookings), 1));