import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-background',
  templateUrl: './background.component.html',
  styleUrls: ['./background.component.scss'],
})
export class BackgroundComponent implements OnInit {

  @ViewChild('canvas', { static: true }) canvasRef: ElementRef;
  private context: CanvasRenderingContext2D;
  private stars: Star[] = [];

  private animationFrameId: number;

  private readonly starDensity = 1; // number of stars per 10000 pixels^2
  private readonly starSize = 1; // star size in pixels
  private readonly starSpeed = 1; // star speed in pixels per frame
  private readonly maxStarOpacity = 1;
  private readonly minStarOpacity = 0.1;

  ngOnInit() {
    // initialize the canvas and context
    this.context = this.canvasRef.nativeElement.getContext('2d');

    // resize canvas to match the window size
    this.canvasRef.nativeElement.width = window.innerWidth;
    this.canvasRef.nativeElement.height = window.innerHeight;

    // create stars
    const starCount = Math.round(this.starDensity * this.canvasRef.nativeElement.width * this.canvasRef.nativeElement.height / 10000);
    for (let i = 0; i < starCount; i++) {
      const x = Math.random() * this.canvasRef.nativeElement.width;
      const y = Math.random() * this.canvasRef.nativeElement.height;
      const opacity = Math.random() * (this.maxStarOpacity - this.minStarOpacity) + this.minStarOpacity;
      this.stars.push(new Star(x, y, this.starSize, opacity, this.starSpeed, Math.max(this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height) / 2));
    }

    // start animation
    this.animationFrameId = requestAnimationFrame(() => this.draw());
  }

  draw() {
    // clear canvas
    this.context.clearRect(0, 0, this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height);

    // draw stars
    for (const star of this.stars) {
      star.draw(this.context);
    }

    // request next frame
    this.animationFrameId = requestAnimationFrame(() => this.draw());
  }
}

class Star {
  private x: number;
  private y: number;
  private size: number;
  private opacity: number;
  private speed: number;
  private colorOn: string = '#fff600';
  private colorOff: string = '#FFF';
  private color: string = this.colorOff;
  private maxDistance: number;
  private centerX: number;
  private centerY: number;
  private showing: boolean = false;
  private flickering: boolean = false;
  private flickerStart: number;

  constructor(x: number, y: number, size: number, opacity: number, speed: number, maxDistance: number) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.opacity = opacity;
    this.speed = speed;
    this.maxDistance = maxDistance;
    this.centerX = x;
    this.centerY = y;
  }

  update(mouseX: number, mouseY: number) {
    const xDiff = mouseX - this.centerX;
    const yDiff = mouseY - this.centerY;
    const distance = Math.sqrt(xDiff ** 2 + yDiff ** 2);

    if (distance < this.maxDistance) {
      this.opacity = 1 - distance / this.maxDistance;
      if (!this.flickering && Math.random() < 0.02) {
        this.flickering = true;
        this.flickerStart = Date.now();
      }
    } else {
      this.opacity = 0;
    }

    if (this.opacity > 0) {
      this.color = this.colorOn;
    } else {
      this.color = this.colorOff;
    }

    if (this.showing && this.opacity <= 0) {
      this.showing = false;
    } else if (!this.showing && this.opacity > 0) {
      this.showing = true;
      setTimeout(() => {
        this.color = this.colorOff;
      }, 500);
    }

    if (this.flickering) {
      const timeElapsed = Date.now() - this.flickerStart;
      if (timeElapsed > 100) {
        this.flickering = false;
      } else if (Math.random() < 0.5) {
        this.color = this.colorOff;
      } else {
        this.color = this.colorOn;
      }
    }
  }

  draw(context: CanvasRenderingContext2D) {
    context.save();
    context.beginPath();
    context.arc(this.x, this.y, this.size, 0, Math.PI * 2);
    context.fillStyle = this.color;
    context.globalAlpha = this.opacity;
    context.shadowBlur = 10;
    context.shadowColor = '#FFF';
    context.fill();
    context.closePath();
    context.restore();
  }
}
