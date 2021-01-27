library(data.table)
library(rnn)

path <- "../../data/csv/log.csv"

data <- read.csv(path, sep=";")
setDT(data)

# Feature selection
ts <- data$TS
data$TS <- NULL # remove TS
label <- data$LABEL
data$LABEL <- NULL

xArray <- 

model <- rnn::trainr(Y=label, X=array(data, ts))
