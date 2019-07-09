package com.example.kri.chess;

public class Geometry{

    public static class Point{
        public final float x, y, z;

        public Point(float x, float y, float z){this.x = x;this.y = y;this.z = z;}

        public Point translateY(float distance){return new Point(x, y + distance, z);}

        public Point translate(Vector vector){return new Point(x + vector.x, y + vector.y, z + vector.z);}

    }

    public static class Circle{
        public final Point mCenter;
        public final float mRadius;

        public Circle(Point center, float radius){mCenter = center;mRadius = radius;}

        public Circle scale(float scale){return new Circle(mCenter, mRadius * scale);}
    }

    public static class Cylinder{
        public final Point mCenter;
        public final float mRadius;
        public final float mHeight;

        public Cylinder(Point center, float radius, float height){mCenter = center;mRadius = radius;mHeight = height;}
    }

    public static class Ray{
        public final Point mPoint;
        public final Vector mVector;
        public Ray(Point point, Vector vector){mPoint = point;mVector = vector;}
    }

    public static class Vector{
        public final float x, y, z;

        public Vector(float x, float y, float z){this.x = x;this.y = y;this.z = z;}

        public float length(){return (float)Math.sqrt(x * x + y * y + z * z);}

        public Vector crossProduct(Vector other){return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        public float dotProduct(Vector other){return x * other.x + y * other.y + z * other.z;}

        public Vector scale(float f){return new Vector(x * f, y * f, z * f);}

        public Vector normalize(){return new Vector(x/length(), y/length(), z/length());}
    }

    public static Vector vectorBetween(Point from, Point to){
        return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    public static class Sphere{
        public final Point mCenter;
        public final float mRadius;
        public Sphere(Point center, float radius){mCenter = center;mRadius = radius;}
    }

    public static boolean intersects(Sphere sphere, Ray ray){
        return distanceBetween(sphere.mCenter, ray) < sphere.mRadius;
    }

    public static float distanceBetween(Point point, Ray ray){
        Vector p1ToPoint = vectorBetween(ray.mPoint, point);
        Vector p2ToPoint = vectorBetween(ray.mPoint.translate(ray.mVector), point);
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.mVector.length();
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    public static class Plane{
        public final Point mPoint;
        public final Vector mNormal;
        public Plane(Point point, Vector normal){
            mPoint = point;
            mNormal = normal;
        }
    }

    public static Point intersectionPoint(Ray ray, Plane plane){
        Vector rayToPlaneVector = vectorBetween(ray.mPoint, plane.mPoint);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.mNormal)/ray.mVector.dotProduct(plane.mNormal);
        Point intersectionPoint = ray.mPoint.translate(ray.mVector.scale(scaleFactor));
        return intersectionPoint;
    }
}
