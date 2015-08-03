

#################################################
#
#   Forecast of NordPoolSpot Data
#   July 2014
#
#################################################





#############################################
#
# ================
# forecast_arima
# ================
# 
# function for computing forecasts and KPIs 
# for an energy price data set of 2010, 
# applied to the ARIMA model
#
# Parameters:
# -----------
# energy_prices - The energy price dataset
#   on which to operate the models
# offset - specify offset of hours adding to
#   the beginning of the data set
# five_minute - boolean value to indicate
#   whether data is in 5 minute granularity
# 
#############################################


readCSV <- function(relPath) {
  
#   if(!grepl("/R DA/Workspace",getwd())) {
#     setwd("./R DA/Workspace")
#   }
  
  prices <- read.csv(relPath)
  
  h_prices <- head(prices)
  
  return(h_prices)
  
}

# 
# pr <- readCSV("~/R DA/energy data/US States (Drazen)/prices.csv")
# 
# print(head(pr))

#############################################
#
# ================
# forecast_arima
# ================
# 
# function for computing forecasts and KPIs 
# for an energy price data set of 2010, 
# applied to the ARIMA model
#
# Parameters:
# -----------
# energy_prices - The energy price dataset
#   on which to operate the models
# offset - specify offset of hours adding to
#   the beginning of the data set
# five_minute - boolean value to indicate
#   whether data is in 5 minute granularity
# 
#############################################


forecast_arima <- function(energy_prices, offset=0, lag_max=20, five_minute=FALSE) {
  
#   if(!grepl("/R DA/Workspace",getwd())) {
#     setwd("./R DA/Workspace")
#   }
}
  
  