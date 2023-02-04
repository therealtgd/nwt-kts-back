insert into users(id, city, credits, display_name, email, enabled, password, phone_number, provider, provider_user_id, username, image_id)
values (1, 'Novi Sad', 1000, 'Klijent', 'client@gmail.com', true, '$2a$10$tFubb/d6TaBZ1SrE3FCPpOqHY57UknFBZRiVyfHfpKeEQbfSWI/Jm', '0831213123', null, null, 'client', null);
insert into client(is_activated, payment_info, status, id) values (true, '', 'ONLINE', 1);
-- password: client

insert into vehicle(id, babies_allowed, capacity, latitude, licence_plate, longitude, pets_allowed, type)
values (1, true, 4, 45.24146739121831, 'SWAGGER', 19.831773947286283, true, 'SEDAN');

insert into users(id, city, credits, display_name, email, enabled, password, phone_number, provider, provider_user_id, username, image_id)
values (2, 'Novi Sad', 1000, 'Vozac', 'driver@gmail.com', true, '$2a$10$FumyQ0aCG7/Z7FgC2q.gQOoanQzLlBet/Gal9IkwFK9mX3rL.liV6', '0831213123', null, null, 'driver', null);
insert into driver(id, is_reserved, status, vehicle_id) values (2, true, 'ONLINE', 1)
-- password: driver