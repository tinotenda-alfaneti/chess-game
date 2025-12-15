PHONY: build run push deploy clean

IMAGE_NAME = tinorodney/chess-game
TAG = v1.0.0

build:
	docker build -t $(IMAGE_NAME):$(TAG) .

run:
	docker run -p 6080:6080 -p 5900:5900 $(IMAGE_NAME):$(TAG)

push:
	docker push $(IMAGE_NAME):$(TAG)

deploy:
	kubectl create namespace chess-game-ns --dry-run=client -o yaml | kubectl apply -f -
	helm upgrade --install chess-game ./charts/app \
		--namespace chess-game-ns \
		--set image.repository=$(IMAGE_NAME) \
		--set image.tag=$(TAG)

clean:
	docker rmi $(IMAGE_NAME):$(TAG) || true

test-local:
	@echo "Building and running locally..."
	@make build
	@echo "Access the chess game at: http://localhost:6080/vnc.html"
	@make run
