FROM node:alpine

WORKDIR /app

RUN apk add git
RUN git clone https://github.com/Musheer360/QuoteSlate.git .
RUN npm install

EXPOSE 3000

ENTRYPOINT ["npm","start"]