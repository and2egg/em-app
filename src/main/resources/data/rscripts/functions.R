

#' load all necessary libraries
loadLibraries <- function()
{
  library(forecast) # import ARIMA etc. 
  library(MASS)
  library(TSA) # import periodogram etc. 
  library(car) # import qqPlot
  library(xts) # import periodicity and time functions
}


#' Plot time series vs. residuals of given model
#' ------------
#' @param model the model to evaluate
#' @param title the title of the plot
#' @param xlab label for x-axis
#' @param ylab label for y-axis
plotModelAgainstErrors <- function(model, title="Model vs. Residuals", xlab="Time", ylab="Value") {
  # plot the result
  plot(model$x, 
       main=title, 
       xlab=xlab, 
       ylab=ylab, type="l", col=1)
  lines(model$x + model$residuals, type="l", col=2)
}


#' Add significance level to ACF function
#' ----------------
#' idea taken from http://www.squaregoldfish.co.uk/2010/01/20/r-the-acf-function-and-statistical-significance/
#' @param series the series to calculate the ACF for
#' @param main the title of the plot
#' @param type the type of ACF to generate
#' @param ci confidence interval
#' @param lag.max the maximum lag up to which to compute the ACF
#' @param plot boolean value to indicate whether the result should be plotted
#' @param na.action value to define what to do in case NA values are encountered
#' @return a vector consisting of the ACF correlation results plus the calculated significance level
acf_sig <- function(series, main="ACF Series", type="correlation", ci=0.95, lag.max=28, plot=TRUE, na.action=na.pass)
{
  corr <- acf(series,main=main,lag.max=lag.max,type=type,plot=plot,na.action=na.action)
  significance_level <- qnorm((1 + ci)/2)/sqrt(sum(!is.na(series)))
  corr <- c(corr, sig.level=significance_level)
  return(corr)
}

#' Add significance level to PACF function
#' ----------------
#' idea taken from http://www.squaregoldfish.co.uk/2010/01/20/r-the-acf-function-and-statistical-significance/
#' @param series the series to calculate the ACF for
#' @param main the title of the plot
#' @param type the type of ACF to generate
#' @param ci confidence interval
#' @param lag.max the maximum lag up to which to compute the ACF
#' @param plot boolean value to indicate whether the result should be plotted
#' @param na.action value to define what to do in case NA values are encountered
#' @return a vector consisting of the ACF correlation results plus the calculated significance level
pacf_sig <- function(series, main="PACF Series", ci=0.95, lag.max=28, plot=TRUE, na.action=na.pass)
{
  corr <- pacf(series,main=main,lag.max=lag.max,plot=plot,na.action=na.action)
  significance_level <- qnorm((1 + ci)/2)/sqrt(sum(!is.na(series)))
  corr <- c(corr, sig.level=significance_level)
  return(corr)
}



#' function to retrieve the maximum frequency from a given dataset
#' --------------------
#' retrieve the maximum detected frequency (seasonal cycle) if it exists. 
#' In case no seasonality exists the period is 1. 
#' Uses the highest frequency visible in the periodogram
#' of the data to retrieve the periodicity
#' @param data the dataset to examine
#' @param output boolean value to indicate whether results should be printed
#' @param plot boolean value to indicate whether results should be plotted
#' @return the maximum determined frequency in the data
getMaxPeriod <- function(data, output=FALSE, plot=FALSE)
{
  # create a periodogram of the data
  perdgram <- periodogram(data, plot=plot)
  # get the position of the maximum spec value
  pos <- match(max(perdgram$spec),perdgram$spec)
  # access the frequency that occured most often
  freq <- perdgram$freq[pos]
  # transform the frequency into the period length
  period <- 1 / freq
  if(output)
  {
    print(paste("Estimated period:",period))
  }
  return(period)
}


#' function to retrieve the most significant frequencies in the dataset
#' -----------------------
#' Retrieves the most frequent periods and thus different seasonal periods may be estimated
#' If the series indicates white noise behaviour (by white noise threshold)
#' a periodicity of 1 is returned
#' @param data the dataset to examine
#' @param numPeriods the number of periods with the most common frequencies that is returned
#' @param round if resulting periods should be rounded to integers
#' @param output boolean value to indicate whether results should be printed
#' @param plot boolean value to indicate whether results should be plotted
#' @return a vector of numPeriods maximum periods in the data
getMaxPeriods <- function(data, numPeriods=4, round=TRUE, output=FALSE, plot=FALSE)
{
  # create a periodogram of the data
  perdgram <- periodogram(data, plot=plot)
  # Empirically set threshold for white noise
  whiteNoiseTH <- 50
  if(max(perdgram$spec) < whiteNoiseTH)
  {
    return(c(1))
  }
  # get the occurrences of the most frequent periodicities
  sorted_spec <- sort(perdgram$spec, decreasing=TRUE)
  top_freq <- vector(length=numPeriods)
  for (i in 1:numPeriods)
  {
    # get the position of the next sorted spec value in the original vector
    pos <- match(sorted_spec[i],perdgram$spec)
    top_freq[i] <- perdgram$freq[pos] # get the frequency value at this position
    top_freq[i] <- 1 / top_freq[i] # transform to number of periods
    if(round)
    {
      top_freq[i] <- round(top_freq[i]) # truncates number to integer
    }
  }
  if(output)
  {
    print ("Most frequent periods:")
    print (top_freq)
  }
  return (top_freq)
}


#' function to determine the most significant periods from the given dataset
#' ------------------
#' Get the most significant periods from the periodogram of the given dataset. 
#' If a target period is given the most frequent periods of the 
#' dataset are searched for this period, and if it is contained, it is returned. 
#' If the target period is NULL or is not among the most frequent periods
#' a vector with the most common periods is returned
#' @param data the dataset to examine for seasonalities
#' @param target_period the target period to search for
#' @param output boolean value to indicate whether results should be printed
#' @param plot boolean value to indicate whether results should be plotted
getPeriodsWithTarget <- function(data, target_period=24, output=FALSE, plot=FALSE)
{
  periods <- getMaxPeriods(data, output=output, plot=plot)
  if(is.null(target_period)) {
    return(periods) # return most common periods
  }
  if(target_period %in% periods) {
    return(c(target_period))
  } else {
    return(periods)
  }
}



#' automatedBoxTest: Function to compute the Ljung box test to test the given models residuals for white noise
#' ---------------------
#' All needed parameters are chosen based on the values in the provided model
#' rule of thumb for choosing lag -> http://robjhyndman.com/hyndsight/ljung-box-test/
#' or http://www.r-bloggers.com/thoughts-on-the-ljung-box-test/
#' or http://stats.stackexchange.com/questions/6455/how-many-lags-to-use-in-the-ljung-box-test-of-a-time-series
#' Note: h == lag 
#' For non-seasonal time series, use h = min(10, T/5).
#' For seasonal time series, use h = min(2m, round(T/5)).
#' where T is the sample size and m denotes the seasonal period
#' In this case T = 336, 2m = 48 (2 days), round(T/5) = 67
#' @param model the model to test
#' @param lag the given lag. Represents the degrees of freedom of the corresponding
#'        X-squared statistic. If NULL the most suitable value will be estimated
#' @param fitdf fit degrees of freedom. This value is equal to the number of
#'    parameters of the provided model. It will be substracted from the 
#'    degrees of freedom (lag) value of the test. If it is NULL it will be set to the
#'    number of parameters of the model
#'    (@seealso https://www.otexts.org/fpp/2/6)
#' @param type the type name of the Test ("Box-Pierce", "Ljung-Box") (may be abbreviated)
#' @param output boolean value to indicate whether results should be printed to console
#' @return an array (box, lag) where box is the box-test object and lag the number of used lags
automatedBoxTest <- function(model, lag=NULL, fitdf=NULL, 
                             type = c("Box-Pierce", "Ljung-Box"), output=FALSE) {
  
  if(type == "Box-Pierce") {
    
    print("The Box-Pierce test is not supported yet")
    return()
  }
  # length of time series
  t_val <- length(model$x)
  # the frequency stored in the model's time series
  periods <- frequency(model$x)
  
  if(is.null(lag)) {
    if(periods <= 1) { # no seasonality in data
      h <- min(10, t_val/5)
    } else {
      h <- min(2*periods, t_val/5)
    }
  } else {
    h <- lag
  }
  
  h <- round(h)
  
  if(is.null(fitdf)) {
    params <- length(model$coef) # get number of model params
    if("intercept" %in% names(model$coef))
    {
      params <- params - 1
    }
  } else {
    params <- fitdf
  }
  
  if(output) 
  {
    print(paste("Ljung box test with lag",h,"and fitdf of",params))
  }
  
  # do actual box test
  box_t <- Box.test(residuals(model), lag=h, fitdf=params, type=type)
  box_t <- c(box_t, lag=h)
  return(box_t)
}


#' generate_model: function for automatic model generation based on given data values
#' -------------------
#' estimates the occurring frequency in the data, builds and evaluates
#' the model and does a boxcox transformation if necessary 
#' (when data does not appear to have stationary variance)
#' @param data the data series to generate the model upon
#' @param target_period the target period to search for within the periodogram of the dataset
#' @param w_aicc weight for the aicc utility value
#' @param w_ljung weight for the ljung box test p utility value
#' @param boxcox boolean value to indicate whether a boxcox transform should be done
#' @param approximation boolean value to indicate whether model generation should be approximated (faster)
#' @param stepwise boolean value to indicate whether model search should be done stepwise (faster)
#' @param output boolean value to indicate whether results should be printed
#' @param plot boolean value to indicate whether results should be plotted
generate_model <- function(data, target_period=NULL, w_aicc=0.7,
                           w_ljung=0.3, boxcox=TRUE, approximation=TRUE, stepwise=TRUE, output=FALSE, plot=FALSE)
{
  series <- data
  # retrieves the target period if available, otherwise 1
  periods <- getPeriodsWithTarget(series, target_period=target_period, output=output, plot=plot)
  
  # generate list objects
  series_ts <- vector(mode="list", length=length(periods))
  lambda_ts <- vector(mode="list", length=length(periods))
  auto.fit <- vector(mode="list", length=length(periods))
  auto.fit.lambda <- vector(mode="list", length=length(periods))
  box_t <- vector(mode="list", length=length(periods))
  box_t_lambda <- vector(mode="list", length=length(periods))
  
  # time series and lambda parameter generation
  for(i in 1:length(periods)) {
    series_ts[[i]] = ts(series, frequency=periods[[i]])
    lambda_ts[[i]] = BoxCox.lambda(series_ts[[i]])
  }
  
  # model generation and box tests
  for(i in 1:length(periods)) {
    if(output) {
      print("------------------")
      print(paste("Creating model ",i,sep=""))
    }
    auto.fit[[i]] = auto.arima(series_ts[[i]], approximation=approximation, stepwise=stepwise)
    box_t[[i]] <- automatedBoxTest(auto.fit[[i]], type="Ljung", output=output)
  }
  
  if(boxcox == TRUE) {
    # boxcox transformations (if applicable)
    for(i in 1:length(periods)) {
      # if lambda = 1 the model will not be changed
      if(lambda_ts[[i]] != 1)
      {
        if(output) {
          print("------------------")
          print(paste("Creating model ",i," (with BoxCox transformation)",sep=""))
        }
        auto.fit.lambda[[i]] <- auto.arima(series_ts[[i]], lambda=lambda_ts[[i]], approximation=approximation, stepwise=stepwise)
        box_t_lambda[[i]] <- automatedBoxTest(auto.fit.lambda[[i]], type="Ljung", output=output)
      }
      else {
        if(output) {
          print(paste("Not creating model ",i," (lambda == 1)",sep=""))
        }
      }
    }
  }
 
  models <- append(auto.fit, auto.fit.lambda)
  boxtests <- append(box_t, box_t_lambda)
  
  aicc.values <- numeric()
  p.values <- numeric()

  for (i in 1:length(models)) {
    if(!is.null(models[[i]])) {
      aicc.values[i] <- models[[i]]$aicc # appends the current value to the vector  
      p.values[i] <- boxtests[[i]]$p.value # appends the current value to the vector
    }
    else {
      aicc.values[i] <- NA
      p.values[i] <- NA
    }
  }
  
  if(output) {
    print("------------------")
    print("Model aicc values: ")
    print(aicc.values)
    
    print("------------------")
    print("Ljung box test p.values: ")
    print(p.values)
  }
  
  aicc.utility <- numeric()
  p.utility <- numeric()
  result.utility <- numeric()

  # standard significance level for the p-value of the Ljung Box test
  box_test_TH <- 0.05

  AICmin <- aicc.values[which.min(aicc.values)] # get minimum value of aicc values
  
  # calculating utility values
  for (i in 1:length(models)) {
    if(!is.na(aicc.values[i]) && !is.na(p.values)) {
      aicc.utility[i] <- 1 / (abs(AICmin - aicc.values[i]) + 2) # calculate aicc utility value
      p.utility[i] <- (p.values[i] - box_test_TH) / (1 - box_test_TH) # calculate box test utility value
      result.utility[i] <- w_aicc * aicc.utility[i] + w_ljung * p.utility[i] # calculate result utility value
    }
  }
  
  # get index of maximum utility value
  idx <- which.max(result.utility)
  resultModel <- models[[idx]]
  resultTest <- boxtests[[idx]]
  
  if(is.null(resultModel$lambda)) {
    boxcoxTransformed <- FALSE
  } else {
    boxcoxTransformed <- TRUE
  }
  
  if(output)
  {
    print("------------------")
    print("Utility values")
    print("AICc values")
    print(aicc.values)
    print("Ljung Box p-values")
    print(p.values)
    print("AICc utility values")
    print(aicc.utility)
    print("Ljung Box test utility values")
    print(p.utility)
    print("result utility values")
    print(result.utility)
    print("")
    
    print("Resulting model:")
    print("------------------")
    print(paste("BoxCox transformed = ",boxcoxTransformed,", Estimated period = ",period, sep=""))
    print(resultModel$coef)
    print(paste("model parameters (aic,aicc,bic):",resultModel$aic,resultModel$aicc,resultModel$bic))
    print(paste("Ljung box test p-value:",resultTest$p.value))
    print("===================================================")
  }
  
  return(resultModel)
}


#' iterated_box_test: (currently not used) function to generate box tests between a 
#' defined minimum and maximum lag
#' -----------------
#' @return the box test object that exhibited the largest p-value
iterated_box_test <- function(x, lag.min=10, lag.max=80, type = c("Box-Pierce", "Ljung-Box"), fitdf=0)
{
  lag_vec <- lag.min:lag.max
  len <- length(lag_vec)
  results <- vector("list", len)
  max.p.value <- 0
  max.i.pos <- 0
  for (i in 1:len)
  {
    results[[i]] <- Box.test(x, lag=lag_vec[i], fitdf=fitdf, type=type)
    if(results[[i]]$p.value > max.p.value)
    {
      max.p.value <- results[[i]]$p.value
      max.i.pos <- i
    }
  }
  return(results[[max.i.pos]])
}


#' hello_world: test function for R
hello_world <- function(hi) {
  return(paste("Hello, ",hi,sep=""))
}


#' palindrome: test function for R
palindrome <- function(p) {
  for(i in 1:floor(nchar(p)/2) ) {
    r <- nchar(p) - i + 1
    if ( substr(p, i, i) != substr(p, r, r) ) return(FALSE)
  }
  TRUE
}
