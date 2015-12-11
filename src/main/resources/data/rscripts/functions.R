

# load all necessary libraries
loadLibraries <- function()
{
  library(forecast) # import ARIMA etc. 
  library(MASS)
  library(TSA) # import periodogram etc. 
  library(car) # import qqPlot
  library(xts) # import periodicity and time functions
}




### Plot a histogram plus normal distribution of fc errors ###

plotFcErrorHist <- function(forecasterrors, heading="Forecast Error Histogram")
{
  # make a histogram of the forecast errors:
  mybinsize <- IQR(forecasterrors)/4
  mysd   <- sd(forecasterrors)
  mymin  <- min(forecasterrors) - mysd*5
  mymax  <- max(forecasterrors) + mysd*3
  # generate normally distributed data with mean 0 and standard deviation mysd
  mynorm <- rnorm(10000, mean=0, sd=mysd)
  mymin2 <- min(mynorm)
  mymax2 <- max(mynorm)
  if (mymin2 < mymin) { mymin <- mymin2 }
  if (mymax2 > mymax) { mymax <- mymax2 }
  # make a red histogram of the forecast errors, with the normally distributed data overlaid:
  mybins <- seq(mymin, mymax, mybinsize)
  
  hist(forecasterrors, 
       main=heading, 
       xlab="Forecast error distribution", 
       ylab="Density", 
       col="grey", 
       freq=FALSE, 
       breaks=mybins)
  # freq=FALSE ensures the area under the histogram = 1
  
  # generate normally distributed data with mean 0 and standard deviation mysd
  myhist <- hist(mynorm, 
                 main=heading, 
                 xlab="Forecast error distribution", 
                 ylab="Density", 
                 plot=FALSE, 
                 breaks=mybins)
  
  # plot the normal curve as a blue line on top of the histogram of forecast errors:
  points(myhist$mids, myhist$density, type="l", col="black", lwd=1)
}



### RMSE (Root mean square error) to determine the fitness of a model ###
# 
# rmse = sqrt( mean( (obs - sim)^2, na.rm = TRUE) )
# 

RMSE1 <- function(actual, predicted) 
{
  sqrt(mean((actual-predicted)^2))
}

RMSE <- function(errors) 
{
  sqrt(mean((errors)^2))
}

getAccuracyVector <- function(matrices, set, acc_measure)
{
  
  # example
  acc_rmse <- c(acc_rwf['Training set', 'RMSE'])
}


plotModelAgainstErrors <- function(model, title, xlab, ylab) {
  # plot the result
  plot(model$x, 
       main=title, 
       xlab=xlab, 
       ylab=ylab, type="l", col=1)
  lines(model$x + model$residuals, type="l", col=2)
}


# idea taken from http://www.squaregoldfish.co.uk/2010/01/20/r-the-acf-function-and-statistical-significance/

acf_ci <- function(series, main="Acf Series", type="correlation", ci=0.95, lag.max=28, plot=TRUE, na.action=na.pass)
{
  corr <- acf(series,main=main,lag.max=lag.max,type=type,plot=plot,na.action=na.action)
  significance_level <- qnorm((1 + ci)/2)/sqrt(sum(!is.na(series)))
  corr <- c(corr, sig.level=significance_level)
  return(corr)
}

# Add significance level to ACF function

acf_sig <- function(series, main="ACF Series", type="correlation", ci=0.95, lag.max=28, plot=TRUE, na.action=na.pass)
{
  corr <- acf(series,main=main,lag.max=lag.max,type=type,plot=plot,na.action=na.action)
  significance_level <- qnorm((1 + ci)/2)/sqrt(sum(!is.na(series)))
  corr <- c(corr, sig.level=significance_level)
  return(corr)
}

# Add significance level to PACF function

pacf_sig <- function(series, main="PACF Series", ci=0.95, lag.max=28, plot=TRUE, na.action=na.pass)
{
  corr <- pacf(series,main=main,lag.max=lag.max,plot=plot,na.action=na.action)
  significance_level <- qnorm((1 + ci)/2)/sqrt(sum(!is.na(series)))
  corr <- c(corr, sig.level=significance_level)
  return(corr)
}



# function to compute the frequency of seasonality
# in the dataset, if it exists. In case no seasonality
# exists the period is 1. 
# Uses the highest frequency visible in the periodogram
# of the data to retrieve the periodicity
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


# function to compute the frequency of seasonality
# in the dataset, if it exists. Retrieves the four most frequent
# periods and thus different seasonal periods may be estimated
getPeriods <- function(data, round=TRUE, output=FALSE, plot=FALSE)
{
  # create a periodogram of the data
  perdgram <- periodogram(data, plot=plot)
  # get the occurrences of the most frequent periodicities
  sorted_spec <- sort(perdgram$spec, decreasing=TRUE)
  top_freq <- vector(length=4)
  for (i in 1:4)
  {
    # get the position of the next spec value in the original vector
    pos <- match(sorted_spec[i],perdgram$spec)
    top_freq[i] <- perdgram$freq[pos]
    top_freq[i] <- 1 / top_freq[i] # transform to period length
    if(round)
    {
      top_freq[i] <- round(top_freq[i])
    }
  }
  if(output)
  {
    print ("Most frequent periods:")
    print (top_freq)
  }
  return (top_freq)
}


# function to get the most significant period from the periodogram of the
# given dataset. Given a target period the most frequent periods of the 
# dataset are searched for this period, if it is contained, it is returned. 
# Otherwise, the number 1 is returned, which is the standard frequency 
# for any time series
getPeriod <- function(data, target_period=24, output=FALSE, plot=FALSE)
{
  periods <- getPeriods(data, output=output, plot=plot)
  if(checkPeriods(periods, target_period) == TRUE)
  {
    return(target_period)
  }
  else
  {
    return(1)
  }
}


# function to check if the target_period is one of the most
# frequent periods in the dataset. Returns TRUE if it is, FALSE otherwise
checkPeriods <- function(periods, target_period=24)
{
  if(target_period %in% periods) 
  {
    return(TRUE)
  }
  else
  {
    return(FALSE)
  }
}


# Function to compute the Ljung box test to test the given models
# residuals for white noise
# All needed parameters are chosen based on the values in the provided model
#
# rule of thumb for choosing lag -> http://www.r-bloggers.com/thoughts-on-the-ljung-box-test/
# or http://robjhyndman.com/hyndsight/ljung-box-test/
# or http://stats.stackexchange.com/questions/6455/how-many-lags-to-use-in-the-ljung-box-test-of-a-time-series
# For non-seasonal time series, use h = min(10, T/5).
# For seasonal time series, use h = min(2m, T/5).
# where T is the sample size and m denotes the seasonal period
# In this case T = 336, 2m = 48, T/5 = 67
automatedBoxTest <- function(model, lag=NULL, fitdf=NULL, nonseasonal.lag=TRUE, 
                             type = c("Box-Pierce", "Ljung-Box"), output=FALSE)
{
  if(type == "Box-Pierce")
  {
    print("The Box-Pierce test is not supported yet")
    return()
  }
  t_val <- length(model$x)
  periods <- frequency(model$x)
  
  if(is.null(lag))
  {
    if(nonseasonal.lag==FALSE)
    {
      h <- t_val/5
    }
    # no seasonality in data
    if(periods == 1)
    {
      h <- min(10, t_val/5)
    }
    else
    {
      h <- min(2*periods, t_val/5)
    }
  }
  else
  {
    h <- lag
  }
  
  if(is.null(fitdf))
  {
    # get number of model params
    params <- length(model$coef)
    if("intercept" %in% names(model$coef))
    {
      params <- params - 1
    }
  }
  else
  {
    params <- fitdf
  }
  
  if(output) 
  {
    print(paste("Ljung box test with lag",h,"and fitdf of",params))
  }
  
  # do actual box test
  box_t <- Box.test(residuals(model), lag=h, fitdf=params, type="Ljung")
  box_t <- c(box_t, lag=h)
  return(box_t)
}


# function for automatic model generation based on given data values
# estimates the occurring frequency in the data, builds and evaluates
# the model and does a boxcox transformation if necessary 
# (when data does not appear to have stationary variance)
generate_model <- function(data, target_period=24, approximation=TRUE, stepwise=TRUE, output=FALSE, plot=FALSE)
{
  series <- data
  # retrieves the target period if available, otherwise 1
  period <- getPeriod(series, target_period=target_period, output=output, plot=plot)
  series_ts <- ts(series, frequency=period)
  
  lambda_ts <- BoxCox.lambda(series_ts)
  
  if(output) {
    print("Create model 1")
  }
  auto.fit <- auto.arima(series_ts, approximation=approximation, stepwise=stepwise)
  if(output) {
    print("Create model 2 (coxbox transformation)")
  }
  auto.fit.lambda <- auto.arima(series_ts, lambda=lambda_ts, approximation=approximation, stepwise=stepwise)

  box_t <- automatedBoxTest(auto.fit, type="Ljung", output=output)
  box_t.lambda <- automatedBoxTest(auto.fit.lambda, type="Ljung", output=output)
  
  models <- list(m1=auto.fit, m2=auto.fit.lambda) # , m3=auto.fit.no_period, m4=auto.fit.no_period.lambda
  boxtests <- list(b1=box_t, b2=box_t.lambda) # , b3=box_t.no_period, b4=box_t.no_period.lambda
  p.values <- c(boxtests[[1]]$p.value, boxtests[[2]]$p.value) #, boxtests[[3]]$p.value, boxtests[[4]]$p.value
  
  if(output) {
    print("Ljung box test p.values: ")
    print(p.values)
  }
  
  # compare output of the Ljung box test to determine
  # which model exhibits a more advantageous distribution
  # within the residuals
  pos <- match(max(p.values), p.values)
  resultModel <- models[[pos]]
  resultTest <- boxtests[[pos]]
  boxcox <- FALSE
  if(pos == 2)
    boxcox = TRUE
  
  if(output)
  {
    print("Resulting model:")
    print(paste("BoxCox transformed = ",boxcox,", Estimated period = ",period, sep=""))
    print(resultModel$coef)
    print(paste("model parameters (aic,aicc,bic):",resultModel$aic,resultModel$aicc,resultModel$bic))
    print(paste("Ljung box test p-value:",resultTest$p.value))
    print("-------------------")
  }
  
  return(resultModel)
}



# function for automatic model generation based on given data values
# estimates the occurring frequency in the data, builds and evaluates
# the model and does a boxcox transformation if necessary 
# (when data does not appear to have stationary variance)
generate_model_ <- function(data, same.lag=TRUE, approximation=TRUE, stepwise=TRUE, output=FALSE, plot=FALSE)
{
  series <- data
  
  series_ts <- ts(series, frequency=getPeriod(series, target_period=24, output=output, plot=plot))
#   series_ts_no.freq <- ts(series)
  
  lambda_ts <- BoxCox.lambda(series_ts)
#   lambda_ts_no.freq <- BoxCox.lambda(series_ts_no.freq)
  
  if(output) {
    print("Create model 1")
  }
  auto.fit <- auto.arima(series_ts, approximation=approximation, stepwise=stepwise)
  if(output) {
    print("Create model 2")
  }
  auto.fit.lambda <- auto.arima(series_ts, lambda=lambda_ts, approximation=approximation, stepwise=stepwise)
#   print("Create model 3")
#   auto.fit.no_period <- auto.arima(series_ts_no.freq, approximation=approximation, stepwise=stepwise)
#   print("Create model 4")
#   auto.fit.no_period.lambda <- auto.arima(series_ts_no.freq, lambda=lambda_ts_no.freq, approximation=approximation, stepwise=stepwise)
  
  box_t <- automatedBoxTest(auto.fit, type="Ljung", output=output)
  box_t.lambda <- automatedBoxTest(auto.fit.lambda, type="Ljung", output=output)
  
#   if(same.lag==TRUE)
#   {
#     box_t.no_period <- automatedBoxTest(auto.fit.no_period, lag=box_t$lag, type="Ljung", output=output)
#     box_t.no_period.lambda <- automatedBoxTest(auto.fit.no_period.lambda, lag=box_t$lag, type="Ljung", output=output)
#   }
#   else
#   {
#     box_t.no_period <- automatedBoxTest(auto.fit.no_period, type="Ljung", output=output)
#     box_t.no_period.lambda <- automatedBoxTest(auto.fit.no_period.lambda, type="Ljung", output=output)
#   }
  
  models <- list(m1=auto.fit, m2=auto.fit.lambda) # , m3=auto.fit.no_period, m4=auto.fit.no_period.lambda
  boxtests <- list(b1=box_t, b2=box_t.lambda) # , b3=box_t.no_period, b4=box_t.no_period.lambda
  p.values <- c(boxtests[[1]]$p.value, boxtests[[2]]$p.value) #, boxtests[[3]]$p.value, boxtests[[4]]$p.value
  
  if(output) {
    print("p.values: ")
    print(p.values)
  }
  
  # compare output of the Ljung box test to determine
  # which model exhibits a more advantageous distribution
  # within the residuals
  pos <- match(max(p.values), p.values)
  resultModel <- models[[pos]]
  resultTest <- boxtests[[pos]]

  if(output)
  {
    print("Resulting model:")
    print(resultModel$coef)
    print(paste("model parameters (aic,aicc,bic):",resultModel$aic,resultModel$aicc,resultModel$bic))
    print(paste("test p-value:",resultTest$p.value))
  }
  
  return(resultModel)
}



# not really useful, only one specific lag is examined
# see function automatedBoxTest
avgBoxTest <- function(x, lag.min=10, lag.max=80, type = c("Box-Pierce", "Ljung-Box"), fitdf=0)
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


hello_world <- function(hi) {
  return(paste("Hello, ",hi,sep=""))
}


palindrome <- function(p) {
  for(i in 1:floor(nchar(p)/2) ) {
    r <- nchar(p) - i + 1
    if ( substr(p, i, i) != substr(p, r, r) ) return(FALSE)
  }
  TRUE
}


myacf <- function (x, lag.max = NULL, type = c("correlation", "covariance", "partial"), plot = TRUE, na.action = na.fail, demean = TRUE, ...) 
{
  type <- match.arg(type)
  if (type == "partial") {
    m <- match.call()
    m[[1L]] <- quote(stats::pacf)
    m$type <- NULL
    return(eval(m, parent.frame()))
  }
  series <- deparse(substitute(x))
  x <- na.action(as.ts(x))
  x.freq <- frequency(x)
  x <- as.matrix(x)
  if (!is.numeric(x)) 
    stop("'x' must be numeric")
  sampleT <- as.integer(nrow(x))
  nser <- as.integer(ncol(x))
  if (is.na(sampleT) || is.na(nser)) 
    stop("'sampleT' and 'nser' must be integer")
  if (is.null(lag.max)) 
    lag.max <- floor(10 * (log10(sampleT) - log10(nser)))
  lag.max <- as.integer(min(lag.max, sampleT - 1L))
  if (is.na(lag.max) || lag.max < 0) 
    stop("'lag.max' must be at least 0")
  if (demean) 
    x <- sweep(x, 2, colMeans(x, na.rm = TRUE), check.margin = FALSE)
  lag <- matrix(1, nser, nser)
  lag[lower.tri(lag)] <- -1
  acf <- .Call(C_acf, x, lag.max, type == "correlation")
  lag <- outer(0:lag.max, lag/x.freq)
  acf.out <- structure(list(acf = acf, type = type, n.used = sampleT, 
                            lag = lag, series = series, snames = colnames(x)), class = "acf")
  if (plot) {
    plot.acf(acf.out, ...)
    invisible(acf.out)
  }
  else acf.out
}


myplotacf <- function (x, ci = 0.95, type = "h", xlab = "Lag", ylab = NULL, 
                       ylim = NULL, main = NULL, ci.col = "blue", ci.type = c("white", "ma"), max.mfrow = 6, ask = Npgs > 1 && dev.interactive(), 
                       mar = if (nser > 2) c(3, 2, 2, 0.8) else par("mar"), oma = if (nser > 2) c(1, 1.2, 1, 1) else par("oma"), mgp = if (nser > 2) c(1.5, 0.6, 0) else par("mgp"), xpd = par("xpd"), 
                       cex.main = if (nser > 2) 1 else par("cex.main"), verbose = getOption("verbose"), 
                       ...) 
{
  ci.type <- match.arg(ci.type)
  if ((nser <- ncol(x$lag)) < 1L) 
    stop("x$lag must have at least 1 column")
  if (is.null(ylab)) 
    ylab <- switch(x$type, correlation = "ACF", covariance = "ACF (cov)", 
                   partial = "Partial ACF")
  if (is.null(snames <- x$snames)) 
    snames <- paste("Series ", if (nser == 1L) 
      x$series
      else 1L:nser)
  with.ci <- ci > 0 && x$type != "covariance"
  with.ci.ma <- with.ci && ci.type == "ma" && x$type == "correlation"
  if (with.ci.ma && x$lag[1L, 1L, 1L] != 0L) {
    warning("can use ci.type=\"ma\" only if first lag is 0")
    with.ci.ma <- FALSE
  }
  clim0 <- if (with.ci) 
    qnorm((1 + ci)/2)/sqrt(x$n.used)
  else c(0, 0)
  Npgs <- 1L
  nr <- nser
  if (nser > 1L) {
    sn.abbr <- if (nser > 2L) 
      abbreviate(snames)
    else snames
    if (nser > max.mfrow) {
      Npgs <- ceiling(nser/max.mfrow)
      nr <- ceiling(nser/Npgs)
    }
    opar <- par(mfrow = rep(nr, 2L), mar = mar, oma = oma, 
                mgp = mgp, ask = ask, xpd = xpd, cex.main = cex.main)
    on.exit(par(opar))
    if (verbose) {
      message("par(*) : ", appendLF = FALSE, domain = NA)
      str(par("mfrow", "cex", "cex.main", "cex.axis", "cex.lab", 
              "cex.sub"))
    }
  }
  if (is.null(ylim)) {
    ylim <- range(x$acf[, 1L:nser, 1L:nser], na.rm = TRUE)
    if (with.ci) 
      ylim <- range(c(-clim0, clim0, ylim))
    if (with.ci.ma) {
      for (i in 1L:nser) {
        clim <- clim0 * sqrt(cumsum(c(1, 2 * x$acf[-1, 
                                                   i, i]^2)))
        ylim <- range(c(-clim, clim, ylim))
      }
    }
  }
  for (I in 1L:Npgs) for (J in 1L:Npgs) {
    dev.hold()
    iind <- (I - 1) * nr + 1L:nr
    jind <- (J - 1) * nr + 1L:nr
    if (verbose) 
      message("Page [", I, ",", J, "]: i =", paste(iind, 
                                                   collapse = ","), "; j =", paste(jind, collapse = ","), 
              domain = NA)
    for (i in iind) for (j in jind) if (max(i, j) > nser) {
      frame()
      box(col = "light gray")
    }
    else {
      clim <- if (with.ci.ma && i == j) 
        clim0 * sqrt(cumsum(c(1, 2 * x$acf[-1, i, j]^2)))
      else clim0
      plot(x$lag[, i, j], x$acf[, i, j], type = type, xlab = xlab, 
           ylab = if (j == 1) 
             ylab
           else "", ylim = ylim, ...)
      abline(h = 0)
      if (with.ci && ci.type == "white") 
        abline(h = c(clim, -clim), col = ci.col, lty = 2)
      else if (with.ci.ma && i == j) {
        clim <- clim[-length(clim)]
        lines(x$lag[-1, i, j], clim, col = ci.col, lty = 2)
        lines(x$lag[-1, i, j], -clim, col = ci.col, lty = 2)
      }
      title(if (!is.null(main)) 
        main
        else if (i == j) 
          snames[i]
        else paste(sn.abbr[i], "&", sn.abbr[j]), line = if (nser > 
                                                              2) 
          1
        else 2)
    }
    if (Npgs > 1) {
      mtext(paste("[", I, ",", J, "]"), side = 1, line = -0.2, 
            adj = 1, col = "dark gray", cex = 1, outer = TRUE)
    }
    dev.flush()
  }
  invisible()
}

