

-- You can use this file to load seed data into the database using SQL statements

--------------------
-- Energy Markets --
--------------------

insert into EnergyMarket (id, name, description) values (1, 'Nord Pool Spot', 'Spot Market responsible for bidding area of northern europe');
insert into EnergyMarket (id, name, description) values (2, 'Belpex', 'Energy market responsible for managing spot prices in the Belgian region');
insert into EnergyMarket (id, name, description) values (3, 'EPEX Spot','EPEX Spot market for managing energy prices for Germany, Austria, Switzerland and France');
insert into EnergyMarket (id, name, description) values (4, 'ISO-NE', 'Energy market responsible for managing spot prices in the New England region');
insert into EnergyMarket (id, name, description) values (5, 'PJM', 'PJM or Pennsylvania-New Jersey-Maryland Interconnection handles energy prices in the mid-eastern region of the US');


--------------------
--    Location    --
--------------------

insert into location (id,name,timezone,em_id) values(1,'Hamina','Europe/Helsinki',1);
insert into location (id,name,timezone,em_id) values(2,'St.Ghislain','Europe/Brussels',2);
insert into location (id,name,timezone,em_id) values(3,'Potsdam','Europe/Berlin',3);
insert into location (id,name,timezone,em_id) values(4,'Portland','America/New_York',4);
insert into location (id,name,timezone,em_id) values(5,'Boston','America/New_York',4);
insert into location (id,name,timezone,em_id) values(6,'Richmond','America/New_York',5);
insert into location (id,name,timezone,em_id) values(7,'Brighton','America/New_York',5);
insert into location (id,name,timezone,em_id) values(8,'Hatfield','America/New_York',5);
insert into location (id,name,timezone,em_id) values(9,'Madison','America/Chicago',5);
insert into location (id,name,timezone,em_id) values(10,'Georgetown','America/New_York',5);
insert into location (id,name,timezone,em_id) values(11,'Stockholm','Europe/Stockholm',1);


--------------------
--    DA Prices   --
--------------------

--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (1,TO_DATE('2015-07-03 04:00', 'YYYY-MM-DD HH24:MI'),1,'hour',450,4,1);
--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (2,TO_DATE('2015-07-03 05:00', 'YYYY-MM-DD HH24:MI'),1,'hour',460,4,1);
--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (10,TO_DATE('2015-07-03 07:00', 'YYYY-MM-DD HH24:MI'),1,'hour',490,4,1);

