# google-playstore-data-analysis
Data pipeline to analise google playstore data using scala and visualisation on grafana, **set up of environment at the end of Readme**

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


### -----------------------------------------------------------------------------------------------------------------

# Set up Environment and what is needed:

### Step 1:
To Run the environment all you need to do is having docker installed on the machine (note that this was done in a Macbook Pro so any compatibility issue must be checked by you). Once you confirm that docker is installed navigate to **Infrastructure** folder and run infradeploy.sh script, this script will prepare both postgres and grafana server and create a virtual network inside docker to run both containers together.

**Note 1:** Create a db.env file inside postgres folder with the following structure (provide names as you want):
```
POSTGRES_DB=dummydb
POSTGRES_USER=dummyuser
POSTGRES_PASSWORD=dummypassword
```

**Note 2:** On the terminal find the IP address of postgres server, use the following commands: _docker network ls_ to find grafana network, and then use _docker network inspect [network id from network ls command]_, with this you should be able to find the ip address of postgres server. This is used on grafana to make the connection to postgres.

**Note 3:** There is a secondary env file that needs to be created, this time for scala (used to manage in a more secure way connections to dbs and services), this needs to be created under /src/main/resources/ and should be called **application.conf**, the structure should be the following, **change only user and password to match what was selected on Note 1**:
```
database {
  rawDbUrl = "jdbc:postgresql://localhost:5432/rawplaystoredb"
  cleanDbUrl = "jdbc:postgresql://localhost:5432/cleanplaystoredb"
  enrichedDbUrl = "jdbc:postgresql://localhost:5432/enrichedplaystoredb"
  user = "dummyuser"
  password = "dummypassword"
  driver = "org.postgresql.Driver"
}
```

### Step 2:
Enable spark to be ran locally on intelliJ activating _Add dependencies with "provided" scope to classpath_ on Settings -> Main -> Edit Configurations -> Modify Options, after this ran the program so everything is installed correctly.

### Step 3:
Open http://localhost:3000/ on your local computer to access grafana server, here you can create a postgres connection to postgres server using postgres connection use the ip address that you find before on **Note 2** and use the port 5432, use also both password and user defined on **Note 1**. After postgres connection being confirmed and working you should be able to add dashboards from the dashboard folder mentioned before.

### Step 4:
Analysis of data using dashboards created or create new ones. This is a preview of what can be seen on the dashboards:

**- App Performance Dashboard:**

<img width="2036" alt="Screenshot 2023-12-01 at 17 01 52" src="https://github.com/zhawi/google-playstore-data-analysis/assets/44621983/e946434d-c2f8-4648-a3fe-1f0201fa8dee">

**- User Sentiment Review Dashboard:**

<img width="2037" alt="Screenshot 2023-12-01 at 18 31 33" src="https://github.com/zhawi/google-playstore-data-analysis/assets/44621983/aa633b0a-f8d2-4478-b060-7d4d30f0392d">

**- Category Analysis Dashboard:**

<img width="2042" alt="Screenshot 2023-12-01 at 19 24 37" src="https://github.com/zhawi/google-playstore-data-analysis/assets/44621983/3ea9211e-4b7f-4154-a444-3e857b933ec6">

**- Market Trends Dashboard:**

<img width="2047" alt="Screenshot 2023-12-01 at 21 14 43" src="https://github.com/zhawi/google-playstore-data-analysis/assets/44621983/28794b46-c31e-45d3-9258-88e23737ce2a">

Thank you.

