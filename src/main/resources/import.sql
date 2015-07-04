

-- You can use this file to load seed data into the database using SQL statements

--------------------
-- Energy Markets --
--------------------

--insert into EnergyMarket (id,name,description) values(1, 'Nord Pool Spot', 'Spot Market responsible for bidding area of northern europe');
--insert into EnergyMarket (id, name, description) values (2, 'ISO-NE', 'Energy market responsible for managing spot prices in the New England region')

--------------------
--    Location    --
--------------------

--insert into location (id,name,em_id) values(1,'Finland',1);
--insert into location (id,name,em_id) values(2,'Sweden',1);
--insert into location (id,name,em_id) values(3,'Maine',2);
--insert into location (id,name,em_id) values(4,'Massachusetts',2);

--------------------
--    DA Prices   --
--------------------

--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (1,TO_DATE('2015-07-03 04:00', 'YYYY-MM-DD HH24:MI'),1,'hour',450,4,1);
--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (2,TO_DATE('2015-07-03 05:00', 'YYYY-MM-DD HH24:MI'),1,'hour',460,4,1);
--insert into da_prices (id,bid_date,interval,interval_unit,price,time_lag,location_id) values (10,TO_DATE('2015-07-03 07:00', 'YYYY-MM-DD HH24:MI'),1,'hour',490,4,1);

