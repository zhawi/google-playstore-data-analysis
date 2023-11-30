# google-playstore-data-analysis
Data pipeline to analise google playstore data using scala and visualisation on grafana

## Step 1: Data Extraction
- **Extract Data: Load the data from both CSV files.**
- file1 contains app details.
- file2 contains user reviews and sentiments.
		
## Step 2: Data Transformation
1. **Clean and Preprocess:**
    - Handle missing values, outliers, and inconsistencies in both datasets.
    - Normalize text data (e.g., case normalization in App names).
2. **Data Integration:**
   - Join file1 and file2 on the App column to integrate app details with user reviews.
3.  **Feature Engineering:**
   - Create new features like average sentiment score per app, categorizing apps by install base (e.g., low, medium, high), etc.
4. **Data Aggregation:**
   - Aggregate data where necessary, like total reviews, average ratings per category, etc.
		
## Step 3: Data Loading
1. **Database Schema Design:**
- Design a PostgreSQL schema that supports your dashboard requirements.
2. **Load Data into PostgreSQL:**
- Use an ETL tool or write code to load the transformed data into PostgreSQL.
		
## Step 4: Dashboard Creation in Grafana
### Dashboard Design:
Plan your dashboards based on the key metrics and insights you want to visualize.

1. Possible Dashboard Ideas:
- **Dashboard 1: App Performance Overview**
Key Metrics: Average Rating, Total Installs, Total Reviews.
Filters: Category, Price range.
- **Dashboard 2: User Sentiment Analysis**
Sentiment distribution (Positive, Negative, Neutral).
Sentiment Polarity and Subjectivity scores.
- **Dashboard 3: Category-wise Analysis**
Distribution of apps, ratings, and reviews across different categories.
Average size and price of apps per category.
- **Dashboard 4: Market Trends**
Trend over time: Ratings, Installs, and Review counts.
New and trending apps based on recent growth in reviews and installs.
- **Dashboard Development:**
Connect Grafana to the PostgreSQL database.
Use SQL queries to fetch data for visualizations.
Create and customize panels in Grafana for each metric or insight.

## Step 5: Testing and Iteration
**Test Dashboards:**
- Ensure that data loads correctly and visualizations are accurate.
- Iterate Based on Feedback:
- Modify and improve dashboards based on user feedback.

## Step 6: Documentation
**Create Documentation:**
- Document the process, database schema, and dashboard functionalities for future reference.