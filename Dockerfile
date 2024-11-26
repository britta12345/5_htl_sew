FROM node AS build_stage
WORKDIR /app

COPY .. .

RUN npm install

RUN npm run build

FROM nginx

COPY --from=build_stage /app/dist /usr/share/nginx/html

EXPOSE 80