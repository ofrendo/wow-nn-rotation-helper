library(dplyr)

pathRoot <- "C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\data\\csv\\"
amount <- 0:99
fileNames <- paste0(pathRoot, paste0("log", amount, ".csv"))
output <- paste0(pathRoot, "logAll.csv")


result <- data.frame()
for (fileName in fileNames) {
  current <- read.csv(fileName, header=T, sep=";", stringsAsFactors=F)
  result <- bind_rows(result, current)
}

result[is.na(result)] <- 0

# move LABEL to last column
tempLABEL <- result$LABEL
result$LABEL <- NULL
result$LABEL <- tempLABEL

write.table(result, output, sep=";", quote=F, row.names=F)
