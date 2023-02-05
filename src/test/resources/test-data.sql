insert into role (id, name) values (1, 'ROLE_ADMIN');
insert into role (id, name) values (2, 'ROLE_CLIENT');
insert into role (id, name) values (3, 'ROLE_DRIVER');
insert into role (id, name) values (4, 'ROLE_USER');

insert into users(id, city, credits, display_name, email, enabled, password, phone_number, provider, provider_user_id, username, image_id)
values (1, 'Novi Sad', 1000, 'Klijent', 'client@gmail.com', true, '$2a$10$tFubb/d6TaBZ1SrE3FCPpOqHY57UknFBZRiVyfHfpKeEQbfSWI/Jm', '0831213123', null, null, 'client', null);
insert into client(is_activated, payment_info, status, id) values (true, '', 'ONLINE', 1);
-- password: client

insert into vehicle(id, babies_allowed, capacity, latitude, licence_plate, longitude, pets_allowed, type)
values (1, true, 4, 45.24146739121831, 'SWAGGER', 19.831773947286283, true, 'SEDAN');

insert into users(id, city, credits, display_name, email, enabled, password, phone_number, provider, provider_user_id, username, image_id)
values (2, 'Novi Sad', 1000, 'Vozac', 'driver@gmail.com', true, '$2a$10$FumyQ0aCG7/Z7FgC2q.gQOoanQzLlBet/Gal9IkwFK9mX3rL.liV6', '0831213123', null, null, 'driver', null);
insert into driver(id, is_reserved, status, vehicle_id) values (2, true, 'AVAILABLE', 1);
-- password: driver
insert into users_authorities(user_id, authorities_id) values (1, 2);
insert into users_authorities(user_id, authorities_id) values (1, 4);
insert into users_authorities(user_id, authorities_id) values (2, 3);
insert into users_authorities(user_id, authorities_id) values (2, 4);

insert into ride (id, distance, end_time, eta, price, start_time, status, driver_id) values
                 (1, 0, 0, 0, 0, 0, 'IN_PROGRESS', 2);